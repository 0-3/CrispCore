package network.reborn.core.Util.Database;

public abstract class MySQLTask {

    protected final MySQLManager manager;

    public MySQLTask(MySQLManager manager) {
        this.manager = manager;
    }

    public abstract void run();

}
