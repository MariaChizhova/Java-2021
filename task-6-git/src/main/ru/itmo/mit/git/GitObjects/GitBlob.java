package ru.itmo.mit.git.GitObjects;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

import static ru.itmo.mit.git.GitConstants.BLOB;

public class GitBlob implements GitObject {

    private final String filename;
    private String hash;
    private final byte[] content;
    private final String root;

    public GitBlob(@NotNull Path root, byte[] content, @NotNull String name) throws IOException {
        this.root = root.toString();
        this.content = content;
        this.filename = name;
        updateHash();
        GitObject.write(this, root);
    }

    @Override
    public void init() throws IOException {
        updateHash();
        GitObject.write(this, Path.of(root));
    }

    @Override
    public String getType() {
        return BLOB;
    }

    @Override
    public String getHash() {
        return hash;
    }

    String getFileName() {
        return filename;
    }

    byte[] getContent() {
        return content;
    }

    public void updateHash() {
        byte[] bytes = new byte[content.length + filename.getBytes().length];
        System.arraycopy(content, 0, bytes, 0, content.length);
        System.arraycopy(filename.getBytes(), 0, bytes, content.length, filename.getBytes().length);
        hash = DigestUtils.sha1Hex(bytes);
    }

}
