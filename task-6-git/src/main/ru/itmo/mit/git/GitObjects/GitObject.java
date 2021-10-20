package ru.itmo.mit.git.GitObjects;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public interface GitObject extends Serializable {

    String getType();

    String getHash();

    void init() throws IOException;

    @NotNull
    static GitObject read(@NotNull Path path) throws GitException {
        try (InputStream inputStream = Files.newInputStream(path);
             ObjectInputStream objInputStream = new ObjectInputStream(inputStream)) {
            GitObject gitObject;
            try {
                gitObject = (GitObject) objInputStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new GitException("Class not found", e);
            }
            objInputStream.close();
            inputStream.close();
            return gitObject;
        } catch (IOException | GitException e) {
            throw new GitException("IOException", e);
        }
    }

    static void writeObj(OutputStream outputStream, @NotNull GitObject gitObject) throws IOException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(gitObject);
        }
    }

    static void write(@NotNull GitBranch gitBranch, @NotNull Path path) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(path.resolve(GitPaths.branchesDirectory).resolve(gitBranch.getName()))) {
            writeObj(outputStream, gitBranch);
        }
    }

    static void write(@NotNull GitObject gitObject, @NotNull Path path) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(path.resolve(GitPaths.objDirectory).resolve(gitObject.getHash()))) {
            writeObj(outputStream, gitObject);
        }
    }
}
