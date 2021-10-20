package ru.itmo.mit.git.GitObjects;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.itmo.mit.git.GitConstants.BRANCH;

public class GitBranch implements GitObject, Serializable {
    private final String root;
    private String hash;
    private final String name;
    private String commit;

    public GitBranch(@NotNull Path root, @NotNull String name, @NotNull String commit) throws IOException {
        this.root = root.toString();
        this.name = name;
        this.commit = commit;
        init();
    }

    @Override
    public void init() throws IOException {
        updateHash();
        GitObject.write(this, Path.of(root));
    }

    public String getName() {
        return name;
    }

    public String getCommit() {
        return commit;
    }

    public String getType() {
        return BRANCH;
    }

    @Override
    public String getHash() {
        return hash;
    }

    private void updateHash() {
        hash = DigestUtils.sha1Hex((name + commit).getBytes());
    }

    public void setCommit(@NotNull String commit) throws IOException {
        this.commit = commit;
        updateHash();
        GitObject.write(this, Paths.get(root));
    }
}
