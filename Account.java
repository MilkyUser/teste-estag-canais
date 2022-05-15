
class Account {
    public final int ag;
    public final int accountNo;
    public final long cpf;
    private final Balance balance;

    Account(int ag, int accountNo, long cpf) throws InvalidCPFException {
        this.ag = ag;
        this.accountNo = accountNo;
        if (!(TransactionOP.checkCPF(cpf))){
            throw new InvalidCPFException(cpf);
        }
        this.cpf = cpf;
        this.balance = new Balance();
    }

    public boolean equals(Account account) {
        return this.ag == account.ag && this.accountNo == account.accountNo;
    }

    protected Balance getBalance(){
        return this.balance;
    }

    protected void addToBalance(int cents) {
        this.balance.addCents(cents);
    }
}
