public class InvalidCPFException extends Exception{
    InvalidCPFException(long cpf){
        super(String.format("%d is not a valid cpf", cpf));
    }
}
