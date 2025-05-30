package myy803.traineeship_management.services;

import myy803.traineeship_management.domainmodel.User;

public interface UserService {

	public void saveUser(User user);
    public boolean isUserPresent(User user);
	public User findById(String username);
}
