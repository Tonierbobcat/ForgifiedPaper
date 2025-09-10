package com.loficostudios.forgified.paper.utils;

import com.loficostudios.forgified.paper.IPluginResources;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourceLoadingUtils {
    /// TBH I don't know what to call this method
    /// This method extracts the folder from the plugin jar to the data folder
    /// I might change this so that the files get loaded into memory instead of being extracted
    /// For now the only implementation for this is in the ItemRegistry class
    public static void extractDataFolderAndUpdate(IPluginResources resources, String folderName, Consumer<File> onFound) {

        /// Delete old folder
        var oldDataFolder = new File(resources.getDataFolder(), folderName);
        if (oldDataFolder.exists()) {
            try (var paths = Files.walk(oldDataFolder.toPath())) {
                paths.sorted(Comparator.reverseOrder())
                        .map(java.nio.file.Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                /// The reason why we are not throwing an error is because the folder isnt there yet
                e.printStackTrace();
            }
        }

        if (!copyFolderFromResources(resources, folderName)) {
            throw new IllegalStateException("Could not copy folder from resources: " + folderName);
        }

        var dataFolder = new File(resources.getDataFolder(), folderName);
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            throw new IllegalStateException("Could not create or find: " + dataFolder.getAbsolutePath());
        }

        assert dataFolder.listFiles() != null;
        try (var paths = Files.walk(dataFolder.toPath())) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        var file = path.toFile();
                        onFound.accept(file);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateResourcePack(IPluginResources resource) {
        try {
            File tempDir = Files.createTempDirectory("mod_assets_pack").toFile();
            File assetsTarget = new File(tempDir, "assets");
            if (!assetsTarget.mkdirs()) {
                throw new IOException("Could not make directory(s) for assets folder");
            }

            ResourceLoadingUtils.copyFolderFromResourcesToTarget(resource, "assets", assetsTarget);
            File mcmeta = new File(tempDir, "pack.mcmeta");

            String mcmetaContent =
                    """
                    {
                      "pack": {
                        "pack_format": 81,
                        "description": "Generated resource pack from ForgifiedPaper"
                      }
                    }
                    """;
            Files.writeString(mcmeta.toPath(), mcmetaContent, StandardCharsets.UTF_8);

            var output = resource.getDataFolder();
            File outputZip = new File(output, "forgifiedpaper_assets.zip");

            zipFolder(tempDir, outputZip);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not create plugin resources. " + e.getMessage());
        }
    }

    private static void zipFolder(File sourceDir, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Path sourcePath = sourceDir.toPath();
            Files.walk(sourcePath).filter(Files::isRegularFile).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString().replace("\\", "/"));
                try {
                    zos.putNextEntry(zipEntry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }
    public static boolean copyFolderFromResources(IPluginResources resources, String folderName) {
        File outDir = new File(resources.getDataFolder(), folderName);
        if (!outDir.exists()) outDir.mkdirs();

        return copyFolderFromResourcesToTarget(resources, folderName, outDir);
//        try (JarFile jar = new JarFile(resources.getJarFile())) {
//            Enumeration<JarEntry> entries = jar.entries();
//            while (entries.hasMoreElements()) {
//                JarEntry entry = entries.nextElement();
//                String name = entry.getName();
//
//                if (name.startsWith(folderName + "/") && !entry.isDirectory()) {
//                    String relativePath = name.substring(folderName.length() + 1);
//                    File outFile = new File(outDir, relativePath);
//                    outFile.getParentFile().mkdirs();
//
//                    try (InputStream is = jar.getInputStream(entry)) {
//                        Files.copy(is, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                    }
//                }
//            }
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
    }

    public static boolean copyFolderFromResourcesToTarget(IPluginResources resources, String folderName, File targetDir) {
        if (!targetDir.exists()) targetDir.mkdirs();

        try (JarFile jar = new JarFile(resources.getJarFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.startsWith(folderName + "/") && !entry.isDirectory()) {
                    String relativePath = name.substring(folderName.length() + 1);
                    File outFile = new File(targetDir, relativePath);
                    outFile.getParentFile().mkdirs();

                    try (InputStream is = jar.getInputStream(entry)) {
                        Files.copy(is, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
