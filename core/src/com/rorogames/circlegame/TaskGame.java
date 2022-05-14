package com.rorogames.circlegame;

public class TaskGame {
    private int argumentCount1, argumentCount2, reward;
    private String describeText;
    public String taskWord1, taskWord2;
    public int taskIndex, wordIndex;

    public int[] word;

    public TaskGame(int arg1, String word1)
    {
        this.taskWord1 = word1;
        argumentCount1 = arg1;
    }
    public TaskGame(int arg1, int arg2, String word1, String word2)
    {
        this.taskWord1 = word1;
        this.taskWord2 = word2;
        argumentCount1 = arg1;
        argumentCount2 = arg2;
    }
    public boolean check(int toArg1)
    {
        return toArg1 >= argumentCount1;
    }
    public boolean check(int toArg1, int toArg2)
    {
        return toArg1 >= argumentCount1 && toArg2 >= argumentCount2;
    }
    public void setReward(int reward)
    {
        this.reward = reward;
    }
    public int getReward()
    {
        return reward;
    }
    public int getArg1()
    {
        return argumentCount1;
    }
    public int getArg2()
    {
        return argumentCount2;
    }
}
