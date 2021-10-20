package ru.itmo.mit.git.GitObjects;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitPathAndHash;
import ru.itmo.mit.git.GitPaths;
import ru.itmo.mit.git.GitStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static ru.itmo.mit.git.GitConstants.BLOB;
import static ru.itmo.mit.git.GitConstants.TREE;

public class GitTree implements GitObject, Serializable {
    private final List<String> children;
    private final String directoryName;
    private String hash;
    private final String root;

    public String getDirectoryName() {
        return directoryName;
    }

    @Override
    public String getType() {
        return TREE;
    }

    @Override
    public String getHash() {
        return hash;
    }

    public GitTree(@NotNull Path root,
            @NotNull String directoryName,
            @NotNull List<String> children)
            throws IOException {
        this.root = root.toString();
        this.directoryName = directoryName;
        this.children = children;
        init();
    }

    @Override
    public void init() throws IOException {
        updateHash();
        GitObject.write(this, Path.of(root));
    }


    private GitTree(@NotNull Path root,
                    @NotNull String directoryName) throws IOException {
        this.root = root.toString();
        this.directoryName = directoryName;
        children = new ArrayList<>();
        updateHash();
        GitObject.write(this, root);
    }

    private void updateHash() {
        StringBuilder content = new StringBuilder(directoryName);
        children.forEach(content::append);
        hash = DigestUtils.sha1Hex(content.toString().getBytes());
    }

    public void updateStatus(@NotNull Path currentPath, @NotNull Set<Path> set, @NotNull GitStatus gitStatus) throws GitException {
        for (String hash : children) {
            GitObject child = GitObject.read(Paths.get(root).resolve(GitPaths.objDirectory).resolve(hash));
            if (child.getType().equals(TREE)) {
                GitTree gitTree = (GitTree) child;
                gitTree.updateStatus(currentPath.resolve(gitTree.getDirectoryName()), set, gitStatus);
            } else {
                GitBlob gitBlob = (GitBlob) child;
                Path path = currentPath.resolve(Paths.get((gitBlob.getFileName())));
                if (set.contains(path)) {
                    continue;
                }
                set.add(path);
                if (Files.exists(path)) {
                    byte[] content;
                    try {
                        content = Files.readAllBytes(path);
                    } catch (IOException e) {
                        throw new GitException("IOException", e);
                    }
                    if (Arrays.equals(content, gitBlob.getContent())) {
                        gitStatus.addUnmodified(path);
                    } else {
                        gitStatus.addModified(path);
                    }
                } else {
                    gitStatus.addRemoved(path);
                }
            }
        }
    }

    public GitTree addPathToTree(@NotNull Path path, @NotNull String hash) throws GitException, IOException {
        if (path.getNameCount() == 0) {
            throw new IllegalArgumentException();
        }
        if (path.getNameCount() == 1) {
            GitBlob gitBlob = (GitBlob) GitObject.read(Paths.get(root).resolve(GitPaths.objDirectory).resolve(hash));
            List<String> newChildren = new ArrayList<>();
            for (String childHash : children) {
                GitObject child = GitObject.read(Paths.get(root).resolve(GitPaths.objDirectory).resolve(childHash));
                if (!child.getType().equals(BLOB) || !((GitBlob) child).getFileName().equals(gitBlob.getFileName())) {
                    newChildren.add(childHash);
                }
            }
            newChildren.add(hash);
            return new GitTree(Paths.get(root), directoryName, newChildren);
        } else {
            List<String> newChildren = new ArrayList<>();
            boolean existsInTree = false;
            String directory = path.getName(0).toString();
            for (String childHash : children) {
                GitObject child = GitObject.read(Paths.get(root).resolve(GitPaths.objDirectory).resolve(childHash));
                if (child.getType().equals(TREE) && ((GitTree) child).getDirectoryName().equals(directory)) {
                    existsInTree = true;
                    newChildren.add(((GitTree) child).addPathToTree(path.subpath(1, path.getNameCount()), hash).getHash());
                } else {
                    newChildren.add(childHash);
                }
            }
            if (!existsInTree) {
                newChildren.add(new GitTree(Paths.get(root), directory).addPathToTree(path.subpath(1, path.getNameCount()), hash).getHash());
            }
            return new GitTree(Paths.get(root), directoryName, newChildren);
        }
    }

    public List<GitPathAndHash> checkoutTree(@NotNull Path currentPath) throws IOException, GitException {
        List<GitPathAndHash> files = new ArrayList<>();
        for (String hash : children) {
            GitObject child = GitObject.read(Paths.get(root).resolve(GitPaths.objDirectory).resolve(hash));
            if (child.getType().equals(BLOB)) {
                Path filePath = currentPath.resolve(((GitBlob) child).getFileName());
                OutputStream outputStream = Files.newOutputStream(filePath);
                outputStream.write(((GitBlob) child).getContent());
                outputStream.close();
                files.add(new GitPathAndHash(filePath, hash));
            } else {
                Path nextDirectory = currentPath.resolve(((GitTree) child).getDirectoryName());
                if (Files.notExists(nextDirectory)) {
                    Files.createDirectory(nextDirectory);
                }
                files.addAll(((GitTree) child).checkoutTree(nextDirectory));
            }
        }
        return files;
    }

}
