package myy803.traineeship_management.domainmodel;

public enum Role {

	STUDENT("Student"),
	COMPANY("Company"),
	PROFESSOR("Professor"),
	COMMITTEE("Committee Member");
	
	
	private final String value;

    private Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
