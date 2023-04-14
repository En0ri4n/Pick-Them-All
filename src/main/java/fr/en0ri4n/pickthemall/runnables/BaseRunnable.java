package fr.en0ri4n.pickthemall.runnables;

public abstract class BaseRunnable implements Runnable
{
    private int counter;
    private int taskId;

    protected BaseRunnable()
    {
        counter = -1;
    }

    protected void resetCounter()
    {
        counter = -1;
    }

    protected void decreaseCounter()
    {
        counter--;
    }

    public int getCounter()
    {
        return counter;
    }

    public boolean isCounter(int count)
    {
        return getCounter() == count;
    }

    public boolean canCount()
    {
        return getCounter() >= 0;
    }

    public void setCounter(int count)
    {
        this.counter = count;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }
}
