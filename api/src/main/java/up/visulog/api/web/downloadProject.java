package up.visulog.api.web;

public class downloadProject extends WebApi{
    public downloadProject(String branche, String format) {
        super("/projects/:id/repository/archive.format?sha=branche".replace("format", format).replace("branche", branche));
    }
}
