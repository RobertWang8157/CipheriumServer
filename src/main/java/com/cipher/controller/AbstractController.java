package com.cipher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;

import java.util.Locale;
@Controller
public abstract class AbstractController {
    @Autowired
    protected MessageSource messageSource;
    public String getMessage(String messageKey, String... args) {
        return messageSource.getMessage(messageKey, args, getUserLocal());
    }

    protected Locale getUserLocal(){
        return LocaleContextHolder.getLocale();
    }
}
