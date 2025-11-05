package com.malaka.aat.internal.service;

import com.malaka.aat.core.exception.custom.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.malaka.aat.internal.model.File;
import com.malaka.aat.internal.model.QuestionOption;
import com.malaka.aat.internal.model.Test;
import com.malaka.aat.internal.model.TestQuestion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordTestParserService {

    private final FileService fileService;

    public Test parseWordToTest(MultipartFile file, Test test) throws IOException {
        if (!isValidWordFile(file)) {
            throw new BadRequestException("Invalid file format. Only .docx files are supported");
        }

        List<TestQuestion> questions = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {

            List<IBodyElement> bodyElements = document.getBodyElements();
            if (bodyElements.isEmpty()) {
                throw new BadRequestException("Word document is empty");
            }

            // Parse questions from document
            questions = parseQuestions(bodyElements, test);

            if (questions.isEmpty()) {
                throw new BadRequestException("No valid questions found in Word document");
            }

            test.setQuestions(questions);
            log.info("Successfully parsed {} questions from Word document", questions.size());

        } catch (IOException e) {
            log.error("Error reading Word file", e);
            throw new BadRequestException("Error reading Word file: " + e.getMessage());
        }

        return test;
    }

    private List<TestQuestion> parseQuestions(List<IBodyElement> bodyElements, Test test) {
        List<TestQuestion> questions = new ArrayList<>();

        int questionNumber = 0;
        int i = 0;

        while (i < bodyElements.size()) {
            IBodyElement element = bodyElements.get(i);

            if (element instanceof XWPFParagraph paragraph) {
                String text = paragraph.getText().trim();

                // Skip empty paragraphs
                if (text.isEmpty() && !hasImage(paragraph)) {
                    i++;
                    continue;
                }

                // Check if this is an answer line (indicates end of current question)
                if (isAnswerLine(text)) {
                    i++;
                    continue;
                }

                // This could be the start of a new question
                questionNumber++;
                QuestionParseResult result = parseQuestionBlock(bodyElements, i, questionNumber, test);
                if (result.question != null) {
                    questions.add(result.question);
                }
                i = result.nextIndex;
            } else {
                i++;
            }
        }

        return questions;
    }


    private boolean hasImage(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            if (!run.getEmbeddedPictures().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private File extractAndSaveImage(XWPFParagraph paragraph, String prefix) {
        try {
            for (XWPFRun run : paragraph.getRuns()) {
                List<XWPFPicture> pictures = run.getEmbeddedPictures();
                if (!pictures.isEmpty()) {
                    XWPFPicture picture = pictures.get(0); // Get first image
                    XWPFPictureData pictureData = picture.getPictureData();

                    byte[] imageBytes = pictureData.getData();
                    String contentType = pictureData.getPackagePart().getContentType();
                    String extension = getExtensionFromContentType(contentType);
                    String filename = prefix + "_" + System.currentTimeMillis() + extension;

                    // Create MultipartFile from image bytes
                    MultipartFile imageFile = new MockMultipartFile(
                            filename,
                            filename,
                            contentType,
                            new ByteArrayInputStream(imageBytes)
                    );

                    // Save image using FileService
                    return fileService.save(imageFile);
                }
            }
        } catch (Exception e) {
            log.error("Error extracting image from paragraph: {}", e.getMessage(), e);
        }
        return null;
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/bmp" -> ".bmp";
            default -> ".jpg";
        };
    }

    private QuestionParseResult parseQuestionBlock(List<IBodyElement> bodyElements, int startIndex, int questionNumber, Test test) {
        QuestionParseResult result = new QuestionParseResult();
        result.nextIndex = startIndex + 1;

        try {
            XWPFParagraph questionParagraph = (XWPFParagraph) bodyElements.get(startIndex);
            String questionLine = questionParagraph.getText().trim();

            // Extract question text and possibly first option if embedded
            QuestionAndFirstOption parsed = extractQuestionAndFirstOption(questionLine);
            String questionText = parsed.questionText;

            if (questionText.isEmpty() && !hasImage(questionParagraph)) {
                log.warn("Empty question text at element {}, skipping", startIndex + 1);
                return result;
            }

            // Check if question paragraph has an actual image
            boolean paragraphHasImage = hasImage(questionParagraph);
            File extractedImage = null;

            if (paragraphHasImage) {
                // Extract and save the image
                extractedImage = extractAndSaveImage(questionParagraph, "question_" + questionNumber);
            }

            // Determine if the image belongs to the question or to the first embedded option
            short questionHasImage = (short) 0;
            File questionImageFile = null;
            File firstOptionImageFile = null;

            if (extractedImage != null) {
                // If there's an embedded first option, the image belongs to that option
                // Empty string is valid for image-only options
                if (parsed.firstOption != null) {
                    firstOptionImageFile = extractedImage;
                } else {
                    // No embedded option, image belongs to the question
                    questionHasImage = (short) 1;
                    questionImageFile = extractedImage;
                    if (questionText.isEmpty()) {
                        questionText = "Image question " + questionNumber;
                    }
                }
            }

            TestQuestion question = new TestQuestion();
            question.setQuestionText(questionText);
            question.setHasImage(questionHasImage);
            question.setQuestionImage(questionImageFile);
            question.setTest(test);

            // Parse options
            List<QuestionOption> options = new ArrayList<>();
            int currentIndex = startIndex + 1;

            // Add first option if it was embedded in question line
            // Allow empty text if there's an image (for image-only options)
            if (parsed.firstOption != null && (!parsed.firstOption.isEmpty() || firstOptionImageFile != null)) {
                QuestionOption option = createOption(parsed.firstOption, 1, question, firstOptionImageFile);
                if (option != null) {
                    options.add(option);
                }
            }

            // Parse remaining paragraphs until we hit Answer: line or another question
            while (currentIndex < bodyElements.size()) {
                IBodyElement element = bodyElements.get(currentIndex);

                if (!(element instanceof XWPFParagraph)) {
                    currentIndex++;
                    continue;
                }

                XWPFParagraph paragraph = (XWPFParagraph) element;
                String line = paragraph.getText().trim();
                boolean hasImageInParagraph = hasImage(paragraph);

                if (line.isEmpty() && !hasImageInParagraph) {
                    currentIndex++;
                    continue;
                }

                // Check if this is the answer line
                if (isAnswerLine(line)) {
                    markCorrectAnswers(line, options);
                    currentIndex++;
                    break;
                }

                // Extract image if present
                File optionImageFile = null;
                if (hasImageInParagraph) {
                    optionImageFile = extractAndSaveImage(paragraph, "option_" + questionNumber + "_" + (options.size() + 1));
                }

                // Check if line has option prefix (a., b., 1., 2., etc.)
                String optionText = extractOptionText(line);
                if (optionText != null) {
                    QuestionOption option = createOption(optionText, options.size() + 1, question, optionImageFile);
                    if (option != null) {
                        options.add(option);
                    }
                } else if (!line.isEmpty() || hasImageInParagraph) {
                    // No prefix - treat entire line as an option (or just image if line is empty)
                    QuestionOption option = createOption(line, options.size() + 1, question, optionImageFile);
                    if (option != null) {
                        options.add(option);
                    }
                }

                currentIndex++;

                // Safety: Stop if we have 5 options (max allowed)
                if (options.size() >= 5) {
                    // If next element is answer, process it before breaking
                    if (currentIndex < bodyElements.size()) {
                        IBodyElement nextElement = bodyElements.get(currentIndex);
                        if (nextElement instanceof XWPFParagraph) {
                            String nextLine = ((XWPFParagraph) nextElement).getText().trim();
                            if (isAnswerLine(nextLine)) {
                                markCorrectAnswers(nextLine, options);
                                currentIndex++;
                            }
                        }
                    }
                    break;
                }
            }

            // Validate options
            if (options.size() < 3) {
                throw new BadRequestException("Question " + questionNumber + " must have at least 3 options (found " + options.size() + ")");
            }
            if (options.size() > 5) {
                throw new BadRequestException("Question " + questionNumber + " cannot have more than 5 options");
            }

            // Verify at least one correct answer
            boolean hasCorrectAnswer = options.stream().anyMatch(questionOption -> questionOption.getIsCorrect() == 1);
            if (!hasCorrectAnswer) {
                throw new BadRequestException("Question " + questionNumber + " must have at least one correct answer marked");
            }

            question.setOptions(options);
            result.question = question;
            result.nextIndex = currentIndex;

        } catch (Exception e) {
            log.error("Error parsing question block starting at line {}: {}", startIndex + 1, e.getMessage());
            throw new BadRequestException("Error parsing question " + questionNumber + ": " + e.getMessage());
        }

        return result;
    }

    private QuestionAndFirstOption extractQuestionAndFirstOption(String line) {
        QuestionAndFirstOption result = new QuestionAndFirstOption();

        // Pattern 1: "Question text ?a. First option text"
        Pattern patternWithText = Pattern.compile("^(.+?)\\?\\s*([a-e]\\.|[1-5]\\.)\\s*(.+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcherWithText = patternWithText.matcher(line);

        if (matcherWithText.matches()) {
            result.questionText = matcherWithText.group(1).trim() + "?";
            result.firstOption = matcherWithText.group(3).trim();
            return result;
        }

        // Pattern 2: "Question text ?a." (option prefix with no text - might be an image)
        Pattern patternNoText = Pattern.compile("^(.+?)\\?\\s*([a-e]\\.|[1-5]\\.)\\s*$", Pattern.CASE_INSENSITIVE);
        Matcher matcherNoText = patternNoText.matcher(line);

        if (matcherNoText.matches()) {
            result.questionText = matcherNoText.group(1).trim() + "?";
            // The option is just an image, use empty string (image will be attached separately)
            result.firstOption = "";
            return result;
        }

        // No embedded option
        result.questionText = line.trim();
        result.firstOption = null;
        return result;
    }

    private String extractOptionText(String line) {
        // Try to match option prefixes: a., b., A), 1., 2), etc.
        Pattern pattern = Pattern.compile("^([a-e]|[1-5])[.)]\\s*(.+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            return matcher.group(2).trim();
        }

        return null; // No option prefix found
    }

    private QuestionOption createOption(String optionText, int optionOrder, TestQuestion question, File imageFile) {
        QuestionOption option = new QuestionOption();
        option.setQuestion(question);
        option.setIsCorrect((short) 0);

        // Check if option has an actual image
        if (imageFile != null) {
            option.setHasImage((short) 1);
            option.setImageFile(imageFile);

            // Use text if provided, otherwise leave empty for image-only options
            if (optionText != null && !optionText.trim().isEmpty()) {
                option.setOptionText(optionText.trim());
            } else {
                option.setOptionText(""); // Empty text for image-only options
            }
        } else {
            // Text-only option
            if (optionText == null || optionText.trim().isEmpty()) {
                return null; // Skip empty options without images
            }
            option.setHasImage((short) 0);
            option.setOptionText(optionText.trim());
        }

        return option;
    }

    private boolean isAnswerLine(String line) {
        String lowerLine = line.toLowerCase();
        return lowerLine.startsWith("answer:") ||
               lowerLine.startsWith("correct answer:") ||
               lowerLine.startsWith("correct:") ||
               lowerLine.startsWith("javob:") ||
               lowerLine.startsWith("to'g'ri javob:");
    }

    private void markCorrectAnswers(String answerLine, List<QuestionOption> options) {
        // Extract answer part after colon
        int colonIndex = answerLine.indexOf(':');
        if (colonIndex == -1) {
            return;
        }

        String answerPart = answerLine.substring(colonIndex + 1).trim().toUpperCase();

        // Split by comma for multiple correct answers
        String[] answers = answerPart.split(",");

        for (String answer : answers) {
            answer = answer.trim();

            // Check if it's a letter (A, B, C, D, E) or number (1, 2, 3, 4, 5)
            if (answer.matches("[A-E]")) {
                int index = answer.charAt(0) - 'A';
                if (index >= 0 && index < options.size()) {
                    options.get(index).setIsCorrect((short) 1);
                }
            } else if (answer.matches("[1-5]")) {
                int index = Integer.parseInt(answer) - 1;
                if (index >= 0 && index < options.size()) {
                    options.get(index).setIsCorrect((short) 1);
                }
            }
        }
    }

    private boolean isValidWordFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return originalFilename != null && originalFilename.endsWith(".docx");
    }

    private static class QuestionParseResult {
        TestQuestion question;
        int nextIndex;
    }

    private static class QuestionAndFirstOption {
        String questionText;
        String firstOption;
    }
}
