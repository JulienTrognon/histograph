package up.visulog.api.web;

public class getBranches extends WebApi{
    public getBranches() {
        super("/projects/:id/repository/branches");
    }
}
