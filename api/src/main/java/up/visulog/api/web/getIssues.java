package up.visulog.api.web;

public class getIssues extends WebApi{
    public getIssues() {
        super("/projects/:id/issues");
    }
}
