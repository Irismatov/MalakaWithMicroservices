package com.malaka.aat.core.util;

public class ServiceUtil {

    public static String convertToPublicUrl(String absolutePath, String baseUrl) {
        if (absolutePath == null) {
            return null;
        }

        // Extract relative path after the base directory
        // Assuming path format: D:/MalakaFiles/images/2025/01/15/abc123.jpg
        // We want: /uploads/images/2025/01/15/abc123.jpg

        String[] pathParts = absolutePath.replace("\\", "/").split("/");
        StringBuilder relativePath = new StringBuilder();

        // Find where the date structure starts (yyyy/mm/dd pattern)
        boolean foundDateStructure = false;
        for (int i = 0; i < pathParts.length; i++) {
            if (!foundDateStructure && pathParts[i].matches("\\d{4}")) {
                foundDateStructure = true;
            }
            if (foundDateStructure) {
                relativePath.append("/").append(pathParts[i]);
            }
        }

        return baseUrl + "/uploads/images" + relativePath.toString();
    }

}
