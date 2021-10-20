package ru.itmo.mit.git;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import static ru.itmo.mit.git.GitConstants.*;

public class GitCliImpl implements GitCli {

    private final GitClient git;
    private final Path directory;
    private PrintStream printStream;

    public GitCliImpl(String workingDir, PrintStream outputStream) {
        directory = Paths.get(workingDir);
        this.printStream = outputStream;
        this.git = new GitClient(directory, this.printStream);
    }

    @Override
    public void runCommand(@NotNull String command, List<String> arguments) throws GitException {
        try {
            switch (command) {
                case INIT:
                    checkCountOfArgument(arguments, 0, 0);
                    git.init(directory);
                    break;
                case ADD:
                    checkCountOfArgument(arguments, 1, -1);
                    for (String arg : arguments) {
                        git.add(directory.resolve(arg));
                    }
                    break;
                case RM:
                    checkCountOfArgument(arguments, 1, -1);
                    for (String arg : arguments) {
                        git.remove(directory.resolve(arg));
                    }
                    break;
                case STATUS:
                    checkCountOfArgument(arguments, 0, 0);
                    git.status();
                    break;
                case LOG:
                    checkCountOfArgument(arguments, 0, 0);
                    git.log();
                    break;
                case COMMIT:
                    checkCountOfArgument(arguments, 1, 1);
                    git.commit(arguments.get(0));
                    break;
                case BRANCH_CREATE:
                    checkCountOfArgument(arguments, 1, 1);
                    git.createBranch(arguments.get(0));
                    break;
                case BRANCH_REMOVE:
                    checkCountOfArgument(arguments, 1, 1);
                    git.removeBranch(arguments.get(0));
                    break;
                case SHOW_BRANCHES:
                    checkCountOfArgument(arguments, 0, 0);
                    git.showBranches();
                    break;
                case MERGE:
                    checkCountOfArgument(arguments, 1, 1);
                    git.merge(arguments.get(0));
                    break;
                // TODO: RESET + CHECKOUT
                default:
                    throw new GitException("No such command exists");
            }
        } catch (IOException e) {
            throw new GitException("IOException", e);
        }
    }

    public void checkCountOfArgument(@NotNull List<@NotNull String> arguments, int minValue, int maxValue) throws GitException {
        int argSize = arguments.size();
        if (argSize < minValue) {
            throw new GitException("The number of arguments below the minimum acceptable value");
        }
        if (argSize > maxValue && maxValue != -1) {
            throw new GitException("The number of arguments above the maximum acceptable value ");
        }
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        printStream = outputStream;
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) {
        throw new UnsupportedOperationException();
    }

}