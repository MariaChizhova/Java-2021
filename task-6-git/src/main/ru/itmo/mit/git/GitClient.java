package ru.itmo.mit.git;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitObjects.*;

import static ru.itmo.mit.git.GitConstants.MASTER;

public class GitClient {

    private final PrintStream printStream;
    private final Path root;
    private final List<GitBranch> gitBranchesList = new LinkedList<>();
    private final String lineSeparator = System.lineSeparator();
    private static final Pattern patternSpace = Pattern.compile(" ");

    public GitClient(Path path, PrintStream printStream) {
        this.root = path;
        this.printStream = printStream;
    }

    public void init(@NotNull Path path) throws IOException, GitException {
        if (Files.exists(path.resolve(GitPaths.rootDirectory))) {
            throw new GitException("Reinitialized existing Git repository");
        }
        Files.createDirectory(path.resolve(GitPaths.rootDirectory));
        Files.createDirectory(path.resolve(GitPaths.objDirectory));
        Files.createDirectory(path.resolve(GitPaths.branchesDirectory));
        Files.createFile(path.resolve(GitPaths.index));
        Files.createFile(path.resolve(GitPaths.head));
        GitCommit gitCommit = new GitCommit(root, "Initial commit", new ArrayList<>());
        GitBranch master = new GitBranch(root, MASTER, gitCommit.getHash());
        gitBranchesList.add(master);
        writeToHeadBranch(master);
        printStream.println("Project initialized");
    }

    public void add(@NotNull Path path) throws GitException, IOException {
        if (!(path.startsWith(root))) {
            throw new GitException("File " + path + " not added because file in another directory.");
        }
        if (!Files.exists(path)) {
            throw new GitException("File " + path + " not added because file doesn't exist.");
        }
        if (Files.isDirectory(path)) {
            throw new GitException("File " + path + " not added because it is directory.");
        }
        GitBlob gitBlob = new GitBlob(root, Files.readAllBytes(path), path.getFileName().toString());
        List<String> lines = Files.readAllLines(root.resolve(GitPaths.index));
        StringBuilder file = new StringBuilder();
        for (String line : lines) {
            String[] strings = patternSpace.split(line);
            if (strings.length != 2) {
                throw new GitException("File " + path + " not added. Index File broken.");
            }
            if (strings[0].equals(path.toString())) {
                continue;
            }
            file.append(line);
            file.append(lineSeparator);
        }
        file.append(path);
        file.append(" ");
        file.append(gitBlob.getHash());
        file.append(lineSeparator);
        try (OutputStream outputStream = Files.newOutputStream(root.resolve(GitPaths.index))) {
            outputStream.write(file.toString().getBytes());
        }
    }

    public void remove(@NotNull Path path) throws GitException {
        if (!path.startsWith(root)) {
            throw new GitException("File in another directory");
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(root.resolve(GitPaths.index));
        } catch (IOException e) {
            throw new GitException("File not removed. Index file is broken", e);
        }
        StringBuilder file = new StringBuilder();
        for (String line : lines) {
            String[] strings = patternSpace.split(line);
            if (strings.length != 2) {
                throw new GitException("File not removed. Index file is broken");
            }
            if (strings[0].equals(path.toString())) {
                continue;
            }
            file.append(line);
            file.append(lineSeparator);
        }
        try (OutputStream outputStream = Files.newOutputStream(root.resolve(GitPaths.index))) {
            outputStream.write(file.toString().getBytes());
        } catch (IOException e) {
            throw new GitException("File not removed. Index file is broken", e);
        }
    }

    public GitStatus status() throws GitException, IOException {
        GitStatus gitStatus = new GitStatus();
        Set<Path> processed = new HashSet<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(root.resolve(GitPaths.index));
        } catch (IOException e) {
            throw new GitException("Index file broken");
        }
        for (String line : lines) {
            String[] strings = patternSpace.split(line);
            if (strings.length != 2) {
                throw new GitException("Index file broken");
            }
            gitStatus.addStaged(Paths.get(strings[0]));
            processed.add(Paths.get(strings[0]));
        }
        ((GitCommit) readFromHeadObjDirectory()).getTree().updateStatus(root, processed, gitStatus);
        try {
            Files.walk(root)
                    .filter(p -> !p.startsWith(root.resolve(GitPaths.rootDirectory)))
                    .filter(path -> !Files.isDirectory(path) && !processed.contains(path))
                    .forEach(gitStatus::addUntracked);
        } catch (IOException e) {
            throw new GitException("Broken files in " + root, e);
        }
        printStream.println("Current branch is '" + getNameOfCurrentBranch() + "'");
        if (!gitStatus.getStaged().isEmpty()) {
            printStream.println("Ready to commit:" + lineSeparator);
            for (Path path : gitStatus.getStaged()) {
                printStream.println("\t" + path);
            }
        }
        if (!gitStatus.getModified().isEmpty()) {
            printStream.println("Files were modified since head commit:" + lineSeparator);
            for (Path path : gitStatus.getModified()) {
                printStream.println("\t" + path);
            }
        }
        if (!gitStatus.getRemoved().isEmpty()) {
            printStream.println("Removed files:" + lineSeparator);
            for (Path path : gitStatus.getRemoved()) {
                printStream.println("\t" + path);
            }
        }
        if (!gitStatus.getUntracked().isEmpty()) {
            printStream.println("Untracked files:" + lineSeparator);
            for (Path path : gitStatus.getUntracked()) {
                printStream.println("\t" + path);
            }
        }
        return gitStatus;
    }

    public void log() throws GitException, IOException {
        GitBranch headBranch = (GitBranch) readFromHeadBranchesDirectory();
        GitCommit lastCommit = (GitCommit) GitObject.read(root.resolve(GitPaths.objDirectory.resolve(headBranch.getCommit())));
        List<GitCommit> commitsInLog = lastCommit.getLog();
        List<GitCommit> allCommits = new ArrayList<>();
        Set<String> hashes = new HashSet<>();
        for (GitCommit commit : commitsInLog) {
            if (!hashes.contains(commit.getHash())) {
                allCommits.add(commit);
            }
            hashes.add(commit.getHash());
        }
        allCommits.sort(Comparator.comparing(GitCommit::getDate));
        for (GitCommit commit : allCommits) {
            System.out.println(commit.getMessage());
            System.out.println("Commit : " + commit.getHash());
            System.out.println("Author : " + commit.getAuthor());
            System.out.println("Date : " + commit.getDate());
            System.out.println();
        }
    }

    void commit(@NotNull String message) throws GitException, IOException {
        Path index = root.resolve(GitPaths.index);
        List<String> lines = Files.readAllLines(index);
        List<GitPathAndHash> pathsAndHashes = new ArrayList<>();
        for (String line : lines) {
            String[] strings = patternSpace.split(line);
            if (strings.length != 2) {
                throw new GitException("Index file is broken");
            }
            pathsAndHashes.add(new GitPathAndHash(Paths.get(strings[0]), strings[1]));
        }
        GitTree tree = ((GitCommit) readFromHeadObjDirectory()).getTree();
        for (GitPathAndHash p : pathsAndHashes) {
            tree = tree.addPathToTree(root.relativize(p.getPath()), p.getHash());
        }
        List<String> parents = new ArrayList<>();
        GitObject getReadFromHeadObjDirectory = readFromHeadObjDirectory();
        parents.add(getReadFromHeadObjDirectory.getHash());
        GitCommit gitCommit = new GitCommit(root, message, parents, tree);
        ((GitBranch) readFromHeadBranchesDirectory()).setCommit(gitCommit.getHash());
        writeToHeadCommitHash(gitCommit.getHash());
    }

    public void createBranch(@NotNull String branchName) throws GitException, IOException {
        if (getBranch(branchName) != null) {
            throw new GitException("Branch already exists");
        }
        gitBranchesList.add(new GitBranch(root, branchName, (readFromHeadObjDirectory()).getHash()));
    }

    public void removeBranch(@NotNull String branchName) throws GitException, IOException {
        if (((GitBranch) readFromHeadBranchesDirectory()).getName().equals(branchName)) {
            throw new GitException("Trying to delete current branch");
        }
        GitBranch gitBranch = getBranch(branchName);
        if (gitBranch == null) {
            throw new GitException("Trying to delete not existing branch");
        }
        gitBranchesList.remove(gitBranch);
        Files.deleteIfExists(root.resolve(GitPaths.branchesDirectory).resolve(branchName));
    }

    public void showBranches() {
        printStream.println("Available branches: ");
        for (GitBranch branch : gitBranchesList) {
            printStream.println(branch.getName());
        }
    }

    public void merge(@NotNull String branchName) throws GitException, IOException {
        GitBranch branchSecond = getBranch(branchName);
        if (branchSecond == null) {
            throw new GitException("Branch " + branchName + " not found");
        }
        GitCommit commitSecond = (GitCommit) GitObject.read(root.resolve(GitPaths.objDirectory).resolve(branchSecond.getCommit()));
        GitBranch branchFirst = (GitBranch) readFromHeadBranchesDirectory();
        if (branchName.equals(branchFirst.getName())) {
            return;
        }
        GitCommit commitFirst = (GitCommit) GitObject.read(root.resolve(GitPaths.objDirectory).resolve(branchFirst.getCommit()));
        List<String> parents = new ArrayList<>();
        parents.add(commitFirst.getHash());
        parents.add(commitSecond.getHash());
        List<GitPathAndHash> listSecond = commitSecond.getTree().checkoutTree(root);
        List<GitPathAndHash> listFirst = commitFirst.getTree().checkoutTree(root);
        Set<Path> pathsSet = new HashSet<>();
        for (GitPathAndHash p : listFirst) {
            pathsSet.add(p.getPath());
        }
        for (GitPathAndHash p : listSecond) {
            if (!pathsSet.contains(p.getPath())) {
                listFirst.add(p);
            }
        }
        GitTree tree = ((GitCommit) readFromHeadObjDirectory()).getTree();
        for (GitPathAndHash p : listFirst) {
            tree = tree.addPathToTree(root.relativize(p.getPath()), p.getHash());
        }
        GitCommit commit = new GitCommit(root, "Branch " + branchSecond.getName() + " merged into " + branchFirst.getName(), parents, tree);
        branchFirst.setCommit(commit.getHash());
        writeToHeadCommitHash(commit.getHash());
        try (OutputStream outputStream = Files.newOutputStream(root.resolve(GitPaths.index))) {
            for (GitPathAndHash p : listFirst) {
                outputStream.write((p.getPath().toString() + " " + p.getHash() + "\n").getBytes());
            }
        }
    }

    private GitObject readFromHeadBranchesDirectory() throws IOException, GitException {
        List<String> lines = Files.readAllLines(root.resolve(GitPaths.head));
        if (lines.size() != 2) {
            throw new GitException("Head file broken");
        }
        Path path = root.resolve(GitPaths.branchesDirectory);
        return GitObject.read(path.resolve(lines.get(0)));
    }

    private GitObject readFromHeadObjDirectory() throws IOException, GitException {
        List<String> lines = Files.readAllLines(root.resolve(GitPaths.head));
        if (lines.size() != 2) {
            throw new GitException("Head file broken");
        }
        Path path = root.resolve(GitPaths.objDirectory);
        return GitObject.read(path.resolve(lines.get(1)));
    }

    private void writeToHeadCommitHash(@NotNull String commitHash) throws GitException, IOException {
        String name = ((GitBranch) readFromHeadBranchesDirectory()).getName();
        try (OutputStream outputStream = Files.newOutputStream(root.resolve(GitPaths.head))) {
            outputStream.write((name + "\n").getBytes());
            outputStream.write((commitHash + "\n").getBytes());
        }
    }

    private void writeToHeadBranch(@NotNull GitBranch gitBranch) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(root.resolve(GitPaths.head))) {
            outputStream.write((gitBranch.getName() + lineSeparator).getBytes());
            outputStream.write((gitBranch.getCommit() + lineSeparator).getBytes());
        }
    }

    private String getNameOfCurrentBranch() throws IOException, GitException {
        return ((GitBranch) readFromHeadBranchesDirectory()).getName();
    }

    private GitBranch getBranch(@NotNull String branchName) {
        for (GitBranch gitBranch : gitBranchesList) {
            if (gitBranch.getName().equals(branchName)) {
                return gitBranch;
            }
        }
        return null;
    }
}