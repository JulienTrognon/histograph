package up.visulog.api.web;

public class getMergeRequest extends WebApi{
    public getMergeRequest() {
        super("/projects/:id/merge_requests");
    }
}
