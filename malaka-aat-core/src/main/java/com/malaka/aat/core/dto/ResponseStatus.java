package com.malaka.aat.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS(0, "Muvaffaqiyatli yakunlandi"),
    VALIDATION_ERROR(1, "Validation error yuzaga keldi"),
    DUPLICATE_ERROR(2, "Ba'zi ma'lumotlar tizimda allaqachon mavjud"),
    NOT_FOUND_ERROR(3, "B'azi ma'lumotlar tizimda topilmadi"),
    LOGIN_ERROR(4, "Username yoki parol xato kiritilgan"),
    MODULE_NOT_SUFFICIENT(5, "Saqlangan modullar soni yetarli emas"),
    NOT_VALID_STATE(6, "Bunday state mavjud emas"),
    MODULE_ALREADY_EXISTS(7, "Bu o'rindagi module allaqachon mavjud"),
    SYSTEM_ERROR(8, "Tizimda xatolik yuzaga keldi"),
    BAD_REQUEST(9, "Berilgan ma'lumotlarda xatolik mavjud"),
    FORBIDDEN(10, "Ruxsat cheklangan"),
    JWT_ERROR(11, "JWT token bilan xatolik yuzaga keldi"),
    CLIENT_ERROR(12, "Tashqi API bilan xatolik yuzaga keldi"),
    EGOV_ERROR(12, "Personallashtirish markazi tizimda xatolik sodir bo‘ldi"),
    ONE_ID_TOKEN_ERROR(13, "One id serveridan token olishda xatolik sodir bo'ldi"),
    ONE_ID_INFO_ERROR(14, "One id dan ma'lumot olishda xatolik sodir bo'ldi")

    ;

    private final int code;
    private final String note;
}
