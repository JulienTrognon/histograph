package up.visulog.api.web;

public class getUser extends WebApi {
    public getUser(String id) {
        super("/users/:id".replace(":id",id));
    }
}
