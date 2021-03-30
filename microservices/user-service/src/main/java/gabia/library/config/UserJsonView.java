package gabia.library.config;

public class UserJsonView {
    public interface Default {} // userName, phone

    public interface Modify extends Default {} // email

    public interface Add extends Modify{} // id, identifier
 }