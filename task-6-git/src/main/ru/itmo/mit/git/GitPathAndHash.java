package ru.itmo.mit.git;

import java.nio.file.Path;
import java.util.Objects;

public class GitPathAndHash {
    private final Path path;
    private final String hash;

    public GitPathAndHash(Path path, String hash) {
        this.path = path;
        this.hash = hash;
    }

    public Path getPath() {
        return path;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GitPathAndHash that = (GitPathAndHash) o;
        return path != null ? path.equals(that.path) : that.path == null && (Objects.equals(hash, that.hash));

    }

    @Override
    public int hashCode() {
        int res = path != null ? path.hashCode() : 0;
        res = 31 * res + (hash != null ? hash.hashCode() : 0);
        return res;
    }

}
