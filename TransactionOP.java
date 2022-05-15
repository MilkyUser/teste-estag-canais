import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// This exercise was (mostly) coded and commented in english to facilitate debbuging.
// This exercise was made by Bruno Leite de Andrade for the Itaú internship recruitment test
// Warning: The input CANNOT contain invalid CPF's, please validate them after generating your tests
// This exercise was made in Java for compliance reasons (I would prefer Python)

// This class is not meant to be instantiated
public class TransactionOP{

    // Dict to store users accounts
    static HashMap<Long, ArrayList<Account>> people = new HashMap<>();

    static private boolean checkForPerson(Long cpf){
        return people.get(cpf) != null;
    }

    // The following method is syncronized to prevent
    // concurrent calls, which could potentially duplicate values
    private synchronized static void transact(
            Integer cents,
            String transactionType,
            Account sender,
            Account receiver
    ) throws InvalidTransactionException, InvalidBalanceOperation {

        // Perform checks to validate the transaction
        if (cents < 0){
            throw new InvalidTransactionException("Cents value cannot be negative");
        }
        if (sender.equals(receiver)){
            throw new InvalidTransactionException("Cannot transact into the same account!");
        }
        if (transactionType.equals("DOC")){
            if (cents < 1000000){
                throw new InvalidTransactionException("Value to olow for DOC transaction");
            }
        }
        if (transactionType.equals("TED")){
            if (!(cents > 500000)) {
                throw new InvalidTransactionException("Value too low for TED transaction");
            }
            if (cents <= 1000000){
                throw new InvalidTransactionException("Value too high for TED transaction");
            }
        }
        if (transactionType.equals("PIX")) {
            if (!(cents < 500000)) {
                throw new InvalidTransactionException("Value too high for PIX transaction");
            }
        }
        sender.addToBalance(-cents);
        receiver.addToBalance(cents);
        System.out.printf("Sua transferência foi realizada com sucesso!\n" +
                "Saldo do emissor: R$%.2f\n" +
                "Saldo do receptor: R$%.2f\n",
                sender.getBalance().getBalance(),
                receiver.getBalance().getBalance()
        );

    }

    public static boolean checkCPF(long cpf){
        if (cpf < 0 || cpf > 99999999999L){
            return false;
        }
        int firstDigit = 0;
        long mul = 10000000000L;
        for (int i = 1; i < 10; i++){
            firstDigit += ((cpf / mul) % 10) * i;
            mul/=10;
        }
        firstDigit = firstDigit%11%10;

        int secondDigit = 0;
        mul = 10000000000L;
        for (int i = 1; i < 11; i++){
            secondDigit += ((cpf / mul) % 10) * (i-1);
            mul/=10;
        }
        secondDigit = secondDigit%11%10;

        return cpf / 10 % 10 == firstDigit && cpf % 10 == secondDigit;
    }

    public static void main(String [] args) throws IOException, InvalidCPFException {

        for (String programArg: args) {

            List<String> transactionBatch;

            try {
                transactionBatch = Files.readAllLines(Path.of(programArg));
            } catch (NoSuchFileException e) {
                System.out.printf(
                        "\nO arquivo \"%s\" não existe ou é inválido!\n\n",
                        programArg
                );
                continue;
            }

            // Ignores the first two lines of an input file
            transactionBatch = transactionBatch.subList(2, transactionBatch.size());

            for (String transaction : transactionBatch) {
                String[] transactionInfo = transaction.split("\\|");
                int senderAg = Integer.parseInt(transactionInfo[4]);
                int senderAccNo = Integer.parseInt(transactionInfo[5]);
                long senderCPF = Long.parseLong(transactionInfo[6].replaceAll("\\.|-", ""));
                int receiverAg = Integer.parseInt(transactionInfo[8]);
                int receiverAccNo = Integer.parseInt(transactionInfo[9]);
                long receiverCPF = Long.parseLong(transactionInfo[10].replaceAll("\\.|-", ""));
                String transactionType = transactionInfo[2];
                int amount = Integer.parseInt(transactionInfo[1].split("\\.")[0]) * 100;
                if (Integer.parseInt(transactionInfo[1].split("\\.")[1]) < 100){
                    amount += Integer.parseInt(transactionInfo[1].split("\\.")[1]);
                } else {
                    System.out.println("Impossível realizar a operação:" +
                            " This system doesn't recognize floating points with more than two decimal precision cases");
                    continue;
                }
                if (!checkForPerson(senderCPF)){
                    people.put(senderCPF, new ArrayList<>());
                }
                if (!checkForPerson(receiverCPF)){
                    people.put(receiverCPF, new ArrayList<>());
                }
                Account senderTempAccount = new Account(senderAg, senderAccNo, senderCPF);
                Account receiverTempAccount = new Account(receiverAg, receiverAccNo, receiverCPF);
                boolean senderHasAcc = false;
                boolean receiverHasAcc = false;
                for (Account acc : people.get(senderCPF)){
                    if (acc.equals(senderTempAccount)){
                        senderTempAccount = acc;
                        senderHasAcc = true;
                        break;
                    }
                }
                for (Account acc : people.get(receiverCPF)){
                    if (acc.equals(receiverTempAccount)){
                        receiverTempAccount = acc;
                        receiverHasAcc = true;
                        break;
                    }
                }
                if (!senderHasAcc){
                    people.get(senderCPF).add(senderTempAccount);
                }
                if (!receiverHasAcc){
                    people.get(receiverCPF).add(receiverTempAccount);
                }
                try {
                    transact(amount, transactionType, senderTempAccount, receiverTempAccount);
                } catch (InvalidTransactionException | InvalidBalanceOperation exception){
                    System.out.println("Impossível realizar a operação: " + exception.getMessage());
                }
            }
        }
    }
}