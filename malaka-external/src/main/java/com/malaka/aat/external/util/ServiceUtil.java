package com.malaka.aat.external.util;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


public class ServiceUtil {

    public static Sort prepareSortByGtOrLt(String gt, String lt) {
        Sort sort;
        if (gt != null && !gt.isBlank()) {
            sort = Sort.by(Sort.Direction.DESC, gt);
        } else if (lt != null && !lt.isBlank()) {
            sort = Sort.by(Sort.Direction.ASC, lt);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "updtime");
        }
        return sort;
    }

    public static PageRequest prepareDefaultPageRequest() {
        return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updtime"));
    }

    public static PageRequest preparePageRequest(int page, int size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updtime"));
    }

    public static String getProjectBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();
    }

//    public static String extractFioFromUser (User user) {
//        StringBuilder sb = new StringBuilder();
//        if (user.getFirstName() != null) {
//            sb.append(user.getFirstName()).append(" ");
//        }
//        if (user.getLastName() != null) {
//            sb.append(user.getLastName()).append(" ");
//        }
//        if (user.getMiddleName() != null) {
//            sb.append(user.getMiddleName()).append(" ");
//        }
//        return sb.toString().trim();
//    }

}
