import ru.itmo.mit.git.GitCli;
import ru.itmo.mit.git.GitCliImpl;
import ru.itmo.mit.git.GitException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws GitException {
        String workingDirName = "playground";
        (new File(workingDirName)).mkdir();
        GitCli git = new GitCliImpl(workingDirName, System.out);
        if (args.length < 1) {
            System.out.println("The number of arguments below the minimum acceptable value");
            System.exit(0);
        }
        List<String> arguments = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        try {
            git.runCommand(args[0], arguments);
        } catch (GitException e) {
            throw new GitException("IOException", e);
        }
    }
}