class Balance {
    private  int cents;
    Balance(){

        // The 'Balance' object only holds cents as it's currency, in order to avoid floating point errors
        this.cents = 0;
    }
    protected Balance(float amount) throws InvalidBalanceOperation {
        int cash = Integer.parseInt(String.valueOf(amount).split("\\.")[0]);
        int cents = Integer.parseInt(String.valueOf(amount).split("\\.")[1]);
        if (cents < 100){
            this.cents = cents + cash * 100;
        } else {
            throw new InvalidBalanceOperation(
                    String.format("The \"cents\" can hold at most the value of 99, %s is greater than 99", cents));
        }
    }
    protected Balance(int cash, int cents) throws InvalidBalanceOperation {
       this.cents = cash;
        if (cents < 100){
            this.cents += cents;
        } else {
            throw new InvalidBalanceOperation(
                    String.format("The \"cents\" can hold at most the value of 99, %s is greater than 99", cents));
        }
    }
    protected float getBalance(){
        return (float) this.cents/100;
    }

    protected int getCents(){
        return this.cents;
    }

    protected void setBalance(float amount) throws InvalidBalanceOperation {
        this.cents = new Balance(amount).getCents();
    }

    protected void setCents(int cents){
        this.cents = cents;
    }

    protected void addCents(int cents){
        this.cents += cents;
    }

}
