package com.loficostudios.forgified.paper.utils;

import joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class VersionHandler<T> {

    private T t;

    private final VersionTarget[] targets;

    private final String defaultClazz;

    public VersionHandler(String defaultClazz, VersionTarget... targets) {
        this.targets = targets;
        this.defaultClazz = defaultClazz;
    }

    public VersionHandler(VersionTarget... targets) {
        this(null, targets);
    }


    public record VersionTarget(String clazz, Type type, int... version) {
        public enum Type {
            EQUALS,
            LESS_THAN,
            LESS_THAN_EQUALS,
            GREATER_THAN_EQUALS,
            GREATER_THAN
        }
        public String versionString() {
            var a = Arrays.stream(version).mapToObj(n -> "" + n).toArray(String[]::new);
            return Strings.join(a, ".").trim();
        }
    }

    @SuppressWarnings("unchecked")
    public void init() {
        String serverVersionStr = Bukkit.getServer().getMinecraftVersion();
        int[] serverVersion = parseVersion(serverVersionStr);
        String className = this.defaultClazz;

        for (VersionTarget versionTarget : targets) {
            int[] targetVersion = versionTarget.version();
            int comparison = compareVersions(serverVersion, targetVersion);

            if (matches(versionTarget, serverVersionStr, comparison)) {
                className = versionTarget.clazz();
                break;
            }
        }

        try {
            Constructor<?> constructor = Class.forName(className).getDeclaredConstructor();
            constructor.setAccessible(true);
            t = (T) constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean matches(VersionTarget versionTarget, String serverVersionStr, int comparison) {
        boolean matches = false;
        switch (versionTarget.type()) {
            case EQUALS -> {
                var versionStr = versionTarget.versionString();
                matches = serverVersionStr.equals(versionStr) || serverVersionStr.startsWith(versionStr + ".");
            }
            case LESS_THAN -> matches = (comparison < 0);
            case GREATER_THAN -> matches = (comparison > 0);
            case LESS_THAN_EQUALS -> matches = (comparison <= 0);
            case GREATER_THAN_EQUALS -> matches = (comparison >= 0);
        }
        return matches;
    }

    private int compareVersions(int[] v1, int[] v2) {
        int maxLength = Math.max(v1.length, v2.length);
        for (int i = 0; i < maxLength; i++) {
            int num1 = (i < v1.length) ? v1[i] : 0;
            int num2 = (i < v2.length) ? v2[i] : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }

    private int[] parseVersion(String versionStr) {
        try {
            return Arrays.stream(versionStr.split("\\."))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (NumberFormatException e) {

            return new int[]{1, 21, 0};
        }
    }

    public @NotNull T get() {
        return t;
    }
}
