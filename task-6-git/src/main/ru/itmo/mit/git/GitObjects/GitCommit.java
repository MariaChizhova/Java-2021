package ru.itmo.mit.git.GitObjects;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitPaths;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ru.itmo.mit.git.GitConstants.COMMIT;

public class GitCommit implements GitObject, Serializable {
    private final String root;
    private final String message;
    private final String author;
    private final Date date;
    private final List<String> parents;
    private final GitTree tree;
    private String hash;
    private final static String USER_NAME = System.getProperty("user.name");

    private GitCommit(@NotNull Path root,
                      @NotNull String message,
                      @NotNull String author,
                      @NotNull Date date,
                      @NotNull List<String> parents,
                      @NotNull GitTree tree) throws IOException {
        this.root = root.toString();
        this.message = message;
        this.author = author;
        this.date = date;
        this.parents = parents;
        this.tree = tree;
        init();
    }

    @Override
    public void init() throws IOException {
        updateHash();
        GitObject.write(this, Path.of(root));
    }

    public GitCommit(@NotNull Path root,
                     @NotNull String message,
                     @NotNull List<String> parents,
                     @NotNull GitTree tree) throws IOException {
        this(root, message, USER_NAME, new Date(), parents, tree);
    }

    public GitCommit(@NotNull Path root,
                     @NotNull String message,
                     @NotNull List<String> parents) throws IOException {
        this(root, message, USER_NAME, new Date(), parents, new GitTree(root, root.getName(root.getNameCount() - 1).toString(), Collections.emptyList()));
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public GitTree getTree() {
        return tree;
    }

    @Override
    public String getType() {
        return COMMIT;
    }

    @Override
    public String getHash() {
        return hash;
    }

    private void updateHash() {
        StringBuilder content = new StringBuilder();
        content.append(message);
        content.append(author);
        content.append(date);
        content.append(parents);
        content.append(tree.getHash());
        parents.forEach(content::append);
        hash = DigestUtils.sha1Hex(content.toString().getBytes());
    }

    public List<GitCommit> getLog() throws GitException {
        List<GitCommit> log = new ArrayList<>();
        log.add(this);
        for (String hash : parents) {
            GitCommit parent = (GitCommit) GitObject.read(Paths.get(root).resolve(GitPaths.objDirectory).resolve(hash));
            log.addAll(parent.getLog());
        }
        return log;
    }

}