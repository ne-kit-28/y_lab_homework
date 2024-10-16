package y_lab.service;

import y_lab.domain.User;

public interface LoginService {

    public User login(String email, String password);

    public void requestPasswordReset(String email);

    public void register(String name, String email, String password);
}
