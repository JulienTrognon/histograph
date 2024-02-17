package up.visulog.api.web;

public class getCommitDiff extends WebApi{

    public getCommitDiff(String commit_sha) {
        super("/projects/:id/repository/commits/:sha/diff".replaceAll(":sha",commit_sha));
    }
}
