package com.roland.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Indexer extends Task {

    private File shaderDirectory;

    private String shaderIndexOutputDirectory;

    public void setShaderDirectory(final String shaderDirectory) {
        this.shaderDirectory = new File(shaderDirectory);
    }

    public void setShaderIndexOutputDirectory(final String shaderIndexOutputDirectory) {
        this.shaderIndexOutputDirectory = shaderIndexOutputDirectory;
    }

    public void execute() {
        URI relativePathBase = shaderDirectory.getParentFile().toURI();
        Path shaderIndexOutputDirectoryPath = Paths.get(shaderIndexOutputDirectory, "shadersIndex.txt");
        try (BufferedWriter destination = new BufferedWriter(new FileWriter(shaderIndexOutputDirectoryPath.toFile()))) {
            List<String> paths = new ArrayList<>();
            handleDirectory(paths, shaderDirectory, relativePathBase);
            for (int i = 0; i < paths.size() - 1; ++i) {
                destination.append(paths.get(i));
                destination.newLine();
            }
            if (!paths.isEmpty()) {
                destination.append(paths.getLast());
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private void handleDirectory(List<String> paths, File directory, URI relativePathBase) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File x : files) {
            if (x.isDirectory()) {
                handleDirectory(paths, x, relativePathBase);
            } else {
                paths.add(relativePathBase.relativize(x.toURI()).getPath());
            }
        }
    }
}
