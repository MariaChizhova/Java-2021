package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GitStatus {
    private final List<Path> modified = new ArrayList<>();
    private final List<Path> unmodified = new ArrayList<>();
    private final List<Path> removed = new ArrayList<>();
    private final List<Path> untracked = new ArrayList<>();
    private final List<Path> staged = new ArrayList<>();

    public List<Path> getModified() {
        return modified;
    }

    public List<Path> getUnmodified() {
        return unmodified;
    }

    public List<Path> getRemoved() {
        return removed;
    }

    public List<Path> getUntracked() {
        return untracked;
    }

    public List<Path> getStaged() {
        return staged;
    }

    public void addModified(@NotNull Path path) {
        modified.add(path);
    }

    public void addUnmodified(@NotNull Path path) {
        unmodified.add(path);
    }

    public void addRemoved(@NotNull Path path) {
        removed.add(path);
    }

    public void addUntracked(@NotNull Path path) {
        untracked.add(path);
    }

    public void addStaged(@NotNull Path path) {
        staged.add(path);
    }
}
