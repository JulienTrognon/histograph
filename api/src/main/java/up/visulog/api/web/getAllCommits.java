package up.visulog.api.web;

public class getAllCommits extends WebApi{

    /* il faudrait repmpalcer par config */
    public getAllCommits() {
        super("/projects/:id/repository/commits");
    }
}
