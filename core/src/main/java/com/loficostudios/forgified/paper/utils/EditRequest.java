package com.loficostudios.forgified.paper.utils;

import com.loficostudios.forgified.paper.gui.FloralGui;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EditRequest {
    private @Nullable FloralGui gui;
    private Consumer<String> callback;
    private @Nullable Component entryMessage;
    private @Nullable Component invalidValueMessage;
    private Predicate<String> valid;

    private EditRequest(@Nullable FloralGui gui, Consumer<String> callback, @Nullable Component entryMessage, @Nullable Component invalidValueMessage, Predicate<String> valid) {
        this.gui = gui;
        this.callback = callback;
        this.entryMessage = entryMessage;
        this.invalidValueMessage = invalidValueMessage;
        this.valid = valid;
    }

    public @Nullable FloralGui getGUI() {
        return gui;
    }

    public Consumer<String> getCallback() {
        return callback;
    }

    public @Nullable Component getEntryMessage() {
        return entryMessage;
    }

    public @Nullable Component getInvalidValueMessage() {
        return invalidValueMessage;
    }

    public boolean validate(String str) {
        if (valid == null)
            return true;
        return valid.test(str);
    }

    public static EditRequest.Builder number() {
        return new EditRequest.Builder().valid(s -> {
                    try {
                        Double.parseDouble(s);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
    }

    public static class Builder {
        private FloralGui gui;
        private Consumer<String> callback;
        private Component entryMessage;
        private Component invalidValueMessage;
        private Predicate<String> valid;

        public Builder callback(Consumer<String> callback) {
            this.callback = callback;
            return this;
        }

        public Builder entryMessage(Component message) {
            this.entryMessage = message;
            return this;
        }

        public Builder invalidValueMessage(Component message) {
            this.invalidValueMessage = message;
            return this;
        }

        public Builder valid(Predicate<String> valid) {
            this.valid = valid;
            return this;
        }

        @Deprecated(forRemoval = true)
        public Builder gui(FloralGui gui) {
            this.gui = gui;
            return this;
        }

        public EditRequest build() {
            Validate.isTrue(callback != null);
            return new EditRequest(gui, callback, entryMessage, invalidValueMessage, valid);
        }
    }
}
