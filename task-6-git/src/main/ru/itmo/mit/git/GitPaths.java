package ru.itmo.mit.git;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GitPaths {
    public static final Path rootDirectory = Paths.get(".myGit");
    public static final Path objDirectory = rootDirectory.resolve("GitObjects");
    public static final Path branchesDirectory = rootDirectory.resolve("GitBranches");
    public static final Path index = rootDirectory.resolve("index");
    public static final Path head = rootDirectory.resolve("HEAD");

}
