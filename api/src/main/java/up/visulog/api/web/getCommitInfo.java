package up.visulog.api.web;

public class getCommitInfo extends WebApi{
    public getCommitInfo(String url, String commit_sha) {
        super("/projects/:id/repository/commits/:sha".replaceAll(":sha",commit_sha));
    }
}
