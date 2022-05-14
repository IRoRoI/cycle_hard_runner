package com.rorogames.circlegame;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Date;
import java.util.Locale;

public class TaskManager {
    private UI ui;
    public float timer;
    private TaskGame currentTask;
    private String[][] taskWord = {
            {"taskFirstGame"},
            {"global", "taskRecordTime"},
            {"taskJumps"},
            {"taskAirTime"},
            {"taskTimesRow", "taskSecondsRow"},
          //  {"global", "taskMoney"},
            {"taskJetCount"},
            {"taskJetTime"},
            {"taskMicroCount"},
            {"taskMicroTime"},
            {"word","taskCompleteWord"}
    };
    private String[] taskTextRU = {
            "Привет! Ты можешь\nвыполнять наши за-\nдания, а можешь\nпроходить уровни.\nПопробуй!",
            "Хей! Я поставил но-\nвый рекорд %d с. Если\nсможешь лучше, то\nзаплачу %d атомов",
            "Сегодня совершил\n%d прыжков.\nУтрешь мне нос?\nЗаплачу %d атомов",
            "Прыгая с ловушки на\nловушку, я провел в\nвоздухе %d секунд.\nБольше? %d атом",
            "Я продержался %d раз\nподряд по %d секунд.\nПовторишь?\n%d атомов",
            //"Я накопил\n%d атомов.\nЕсли накопишь\nбольше, отдам 10%%.\n%d атомов",
            "Хочу полетать с ре-\nактивным ранцем %d\nраз. Составишь ком-\nпанию? %d атомов",
            "Провел с реактивным\nранцем %d секунд.\nА ты больше?\n%d атомов",
            "В микро режиме весе-\nло! Давай со мной!\nЗа %d раз плачу\n%d атомов",
            "Никто не сравнится\nс моим рекордом\nв микро режиме\n%d секунд.\n%d атомов ",
            "Помоги мне собрать\nслово %s\nпо буквам. Заплачу\n%d атомов"
    };
    private String[] taskTextEN = {

            "Hello! you can to\ndo our jobs, or\nyou can go\nthrough the levels.\nTry!",
            "Hey!I set a new\nrecord %d sec.\nIf you can do\nbetter than that\n%d atoms",
            "Today made\n%d jumps.\nWill you can\nbetter? Pay\n%d atoms",
            "Jumping from\ntrap to trap,\ni spent in\nair %d sec.\nMOre? %d atoms",
            "I held out %d\ntimes in a row\nby %d sec.\nWill you repeat\nit? %d atoms",
            //"Я накопил\n%d атомов.\nЕсли накопишь\nбольше, отдам 10%%.\n%d атомов",
            "I want to fly\nwith jet pack\n%d times.\nWith me?\n%d atoms",
            "Spent with jet\npack %d sec.\nMore?\n%d atoms",
            "IN micro mode\nfun! Come with me!\nFor %d times pay\n%d atoms",
            "Nobody can compare\nto my record\nin micro mode\n %d sec.\n%d atoms ",
            "Help me gather\nthe word %s.\nPay\n%d atoms"
    };
    private String[][] taskArguments = {
            {"randomMultiplyInt 1 1 1"},
            {"bestTime","randomSum 11 25"},
            {"randomMultiplyInt 1 3 100"},
            {"randomMultiplyInt 2 7 10"},
            {"randomMultiplyInt 2 4 1", "randomPartInt 15 bestTime 20 40"},
            //{"money", "randomSum 220 400"},
            {"randomMultiplyInt 2 5 1"},
            {"randomMultiplyInt 2 5 10"},
            {"randomMultiplyInt 2 4 1"},
            {"randomMultiplyInt 2 5 10"},
            {"getWord"}
    };
    private String[] taskReward = {
            "randomMultiplyInt 2 2 100",
            "randomMultiplyInt 3 4 110",
            "randomMultiplyInt 1 2 105",
            "randomMultiplyInt 2 3 120",
            "incArg 115",
            //"partArg 0.1",
            "incArg 100",
            "randomMultiplyInt 3 5 105",
            "incArg 150",
            "randomMultiplyInt 3 5 135",
            "incArg 120"
    };
    private String[] taskRequirements = {
            "not",
            "not",
            "not",
            "not",
            "not",
            //"hasMore money 200",
            "has jetpackHas",
            "has jetpackHas",
            "has microHas",
            "has microHas",
            "not"
    };
    private String[] words = {
            "cycle",
            "roro"
    };
    private int[][] wordsNum = {
            {50, 51, 50, 49, 48},
            {105, 106, 105, 106}
    };
    private Array<Integer> taskIndexes;
    /*
    задача 1 - продержаться какое-то количество секунд "Хей! Я поставил новый рекорд time. Если сможешь продержаться больше, заплачу atoms атомов"
    задача 2 - продержаться несколько раз определенное количество секунд "Привет! У меня получилось продержаться times раз по time секунд несколько раз подряд. Если сможешь также, заплачу"
    задача 3 - погибнуть несколько раз в режиме джетпак
    задача 4 - погибнуть несколько раз в редиме микро
    задача 5 - наиграть несколько секунд в режиме джетпак
    задача 6 - наиграть несколько секунд в режиме микро
    задача 7 - не умереть в следущем режиме джетпак
    задача 8 - не умереть в следущем режиме микро
    задача 9 - прыгнуть несколько раз
    задача 10 - провести в воздухе несколько секунд
    задача 11 - пробежать против часовой стрелки несколько секунд
    задача 12 - накопить деньги

     */
    public TaskManager(UI ui)
    {
        this.ui = ui;
        taskIndexes = new Array<Integer>();
        int taskIndexesSize = CircleConstructor.main.preferences.getInteger("taskIndexesSize");
        taskIndexes.clear();
        for(int i = 0; i < taskIndexesSize; i++)
        {
            taskIndexes.add(CircleConstructor.main.preferences.getInteger("taskI" + i));
            System.out.println(CircleConstructor.main.preferences.getInteger("taskI" + i) + " index task");
            CircleConstructor.main.preferences.remove("taskI" + i);
        }
        boolean ass = taskIndexes.size != 0;
        for(int i = 0; i < taskIndexes.size; i++)
        {
            ass &= (taskIndexes.get(i) == 0);
        }
        if(ass)
            taskIndexes.clear();
        if(taskIndexes.size == 0)
            for(int i = 1; i < taskWord.length; i++)
                if(hasRequired(i))
                    taskIndexes.add(i);

        loadData();
        if(CircleConstructor.main.preferences.getInteger("taskFirstGame") == 0)
        {
            currentTask = null;
            timer = 0;
        }
    }

    public void update(float delta)
    {
        timer -= delta;
        if(currentTask != null) {
            if(currentTask.getArg2() == 0) {
                if (currentTask.check(CircleConstructor.main.preferences.getInteger(currentTask.taskWord1))) {
                    completeTask();
                } else
                    ui.refreshTask(CircleConstructor.main.preferences.getInteger(currentTask.taskWord1));
            }
            else{
                if (currentTask.check(CircleConstructor.main.preferences.getInteger(currentTask.taskWord1),CircleConstructor.main.preferences.getInteger(currentTask.taskWord2))) {
                    completeTask();
                } else
                    ui.refreshTask(CircleConstructor.main.preferences.getInteger(currentTask.taskWord1), CircleConstructor.main.preferences.getInteger(currentTask.taskWord2));
            }
        }
        else {
            ui.refreshTaskTimer(timer);
        }
        if(timer <= 0)
        {
            int indx = MathUtils.random(0, taskIndexes.size-1);
            int taskIndex = taskIndexes.get(indx);
            if(CircleConstructor.main.preferences.getInteger("taskFirstGame") == 0)
                taskIndex = 0;
            if(CircleConstructor.main.preferences.getInteger("completedTasks") == 1)
                taskIndex = 1;
            else {
                taskIndexes.removeIndex(indx);
                if (taskIndexes.size <= 0)
                    for (int i = 1; i < taskWord.length; i++)
                        if (hasRequired(i))
                            taskIndexes.add(i);
            }

            int reward = 0;

            if(taskWord[taskIndex].length == 1)
            {
                int arg = getArgument(taskIndex, 0, 1);
                CircleConstructor.main.preferences.putInteger(taskWord[taskIndex][0], 0);
                currentTask = new TaskGame(arg, taskWord[taskIndex][0]);
                reward = getReward(taskIndex, 1,1);
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex], arg, reward));
            }
            else if(taskWord[taskIndex].length == 2 && taskWord[taskIndex][0].equalsIgnoreCase("global"))
            {
                int arg = getArgument(taskIndex, 1, (CircleConstructor.main.preferences.getInteger(taskArguments[taskIndex][0]) > 0 ? CircleConstructor.main.preferences.getInteger(taskArguments[taskIndex][0]) : 1));
                CircleConstructor.main.preferences.putInteger(taskWord[taskIndex][1], CircleConstructor.main.preferences.getInteger(taskArguments[taskIndex][0]));
                currentTask = new TaskGame(arg, taskWord[taskIndex][1]);
                reward = getReward(taskIndex, 1, arg);
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex],arg, reward));
            }
            else if(taskWord[taskIndex].length == 2 && !taskWord[taskIndex][0].equalsIgnoreCase("global") && !taskWord[taskIndex][0].equalsIgnoreCase("word")) {
                int arg1 = getArgument(taskIndex, 0, 1);
                int arg2 = getArgument(taskIndex, 1, 1);
                CircleConstructor.main.preferences.putInteger(taskWord[taskIndex][0], 0);
                CircleConstructor.main.preferences.putInteger(taskWord[taskIndex][1], arg2);
                currentTask = new TaskGame(arg1, arg2, taskWord[taskIndex][0], taskWord[taskIndex][1]);
                reward = getReward(taskIndex, 1, 1);
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex],arg1, arg2, reward));
            }
            else if(taskWord[taskIndex].length == 2 && taskWord[taskIndex][0].equalsIgnoreCase("word"))
            {
                int wordIndex = getArgument(taskIndex, 0, 1);;
                CircleConstructor.main.preferences.putInteger(taskWord[taskIndex][1], 0);
                CircleConstructor.main.letterFind();
                currentTask = new TaskGame(words[wordIndex].length(), taskWord[taskIndex][1]);
                currentTask.word = wordsNum[wordIndex];
                currentTask.wordIndex = wordIndex;
                reward = getReward(taskIndex, 1, 0);
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex],"''" + words[wordIndex] + "''", reward));
            }
            currentTask.setReward(reward);
            currentTask.taskIndex = taskIndex;
            CircleConstructor.main.preferences.flush();
            saveData();
            timer = 10000;
        }
    }
    public void skipTask()
    {
        currentTask = null;
        CircleConstructor.main.taskWord = false;
        timer = 0;
    }
    private void completeTask()
    {
        ui.completeTask();
        CircleConstructor.main.completeTask(currentTask.getReward());
        if(CircleConstructor.main.preferences.getInteger("completedTasks") > 0)
            timer = CircleConstructor.main.getSkill("taskTimeRefresh").skillValue;
        else timer = 0;
        CircleConstructor.main.preferences.putInteger("completedTasks", CircleConstructor.main.preferences.getInteger("completedTasks") + 1);
        CircleConstructor.main.preferences.flush();
        currentTask = null;
    }
    private int getArgument(int taskIndex, int index, int arg)
    {
        int result = 0;
        if(arg != 0) {
            String command = taskArguments[taskIndex][index];
            String[] commands = command.split(" ");

            if (commands[0].equalsIgnoreCase("random")) {
                result = (int)(arg*MathUtils.random(Float.parseFloat(commands[1]), Float.parseFloat(commands[2])));
            }
            else if(commands[0].equalsIgnoreCase("randomMultiplyInt"))
            {
                System.out.println(Float.parseFloat(commands[3]));
                result = (int)(Integer.parseInt(commands[3])*arg*MathUtils.random(Integer.parseInt(commands[1]), Integer.parseInt(commands[2])));
            }
            else if(commands[0].equalsIgnoreCase("randomSum"))
            {
                result = (int)(arg + MathUtils.random(Float.parseFloat(commands[1]), Float.parseFloat(commands[2])));
            }
            else if(commands[0].equalsIgnoreCase("randomPartInt"))
            {
                result = CircleConstructor.main.preferences.getInteger(commands[2])-MathUtils.random(0, Integer.parseInt(commands[1]));
                if(result <= 0)
                    result = MathUtils.random(0, Integer.parseInt(commands[1]));
                if(result > Integer.parseInt(commands[4]))
                    result = MathUtils.random(Integer.parseInt(commands[3]), Integer.parseInt(commands[4]));
            }
            else if(commands[0].equalsIgnoreCase("getWord"))
            {
                result = MathUtils.random(0, words.length-1);
            }
        }
        else{
            result = Integer.parseInt(taskArguments[taskIndex][index]);
        }

        return result;
    }
    private int getReward(int taskIndex, int arg, int toInc)
    {
        int result = 0;
        if(arg != 0) {
            String command = taskReward[taskIndex];
            String[] commands = command.split(" ");

            if (commands[0].equalsIgnoreCase("random")) {
                result = (int)(arg*MathUtils.random(Float.parseFloat(commands[1]), Float.parseFloat(commands[2])));
            }
            else if(commands[0].equalsIgnoreCase("randomMultiplyInt"))
            {
                System.out.println(Float.parseFloat(commands[3]));
                result = (int)(Integer.parseInt(commands[3])*arg*MathUtils.random(Integer.parseInt(commands[1]), Integer.parseInt(commands[2])));
            }
            else if(commands[0].equalsIgnoreCase("partArg"))
            {
                result = (int)(toInc * Float.parseFloat(commands[1]));
            }
            else if(commands[0].equalsIgnoreCase("incArg"))
            {
                result = currentTask.getArg1()*Integer.parseInt(commands[1]);
            }
        }
        else{
            result = Integer.parseInt(taskReward[taskIndex]);
        }

        return result;
    }
    private boolean hasRequired(int taskIndex)
    {
        boolean result = true;
        String command = taskRequirements[taskIndex];
        String[] commands = command.split(" ");
        if(commands[0].equalsIgnoreCase("has"))
        {
            result = CircleConstructor.main.preferences.getBoolean(commands[1]);
        }
        else if(commands[0].equalsIgnoreCase("hasMore"))
        {
            result = CircleConstructor.main.preferences.getInteger(commands[1]) >= Integer.parseInt(commands[2]);
        }
        return result;
    }

    public void saveData()
    {
        Date d = new Date();
        CircleConstructor.main.preferences.putFloat("timer", timer);
        CircleConstructor.main.preferences.putLong("curTime", d.getTime());
        if(currentTask != null && currentTask.taskIndex != 0) {
            CircleConstructor.main.preferences.putInteger("taskIndex", currentTask.taskIndex);
            CircleConstructor.main.preferences.putInteger("taskReward", currentTask.getReward());
            CircleConstructor.main.preferences.putInteger("taskArgument1", currentTask.getArg1());
            CircleConstructor.main.preferences.putInteger("taskArgument2", currentTask.getArg2());
            CircleConstructor.main.preferences.putInteger("taskWordIndex", currentTask.wordIndex);
        }
        else  CircleConstructor.main.preferences.putInteger("taskIndex", -1);

        for(int i = 0; i < CircleConstructor.main.preferences.getInteger("taskIndexesSize"); i++)
        {
            CircleConstructor.main.preferences.remove("taskI"+i);
        }

        CircleConstructor.main.preferences.putInteger("taskIndexesSize", taskIndexes.size);
        for(int i = 0; i< taskIndexes.size; i++)
        {
            CircleConstructor.main.preferences.putInteger("taskI"+i, taskIndexes.get(i));
        }
        CircleConstructor.main.preferences.flush();
    }
    public TaskGame getTask()
    {
        return currentTask;
    }
    public int getLetter(int index)
    {
        return currentTask.word[index];
    }
    public void loadData()
    {
        Date d = new Date();
        long time  = (d.getTime() - CircleConstructor.main.preferences.getLong("curTime"))/1000;
        timer = CircleConstructor.main.preferences.getFloat("timer") - time;
        System.out.println(words[0].length());
        int taskIndex = CircleConstructor.main.preferences.getInteger("taskIndex");

        if(taskIndex != -1)
        {
            System.out.println(taskIndex + "task index");
            if(taskWord[taskIndex].length == 1)
            {
                int arg = CircleConstructor.main.preferences.getInteger("taskArgument1");
                int reward = CircleConstructor.main.preferences.getInteger("taskReward");
                currentTask = new TaskGame(arg, taskWord[taskIndex][0]);
                currentTask.setReward(reward);
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex], arg, reward));
            }
            else if(taskWord[taskIndex].length == 2 && taskWord[taskIndex][0].equalsIgnoreCase("global"))
            {
                int arg = CircleConstructor.main.preferences.getInteger("taskArgument1");
                int reward = CircleConstructor.main.preferences.getInteger("taskReward");
                currentTask = new TaskGame(arg, taskWord[taskIndex][1]);
                currentTask.setReward(reward);
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex],arg, reward));
            }
            else if(taskWord[taskIndex].length == 2 && !taskWord[taskIndex][0].equalsIgnoreCase("global") && !taskWord[taskIndex][0].equalsIgnoreCase("word")) {
                int arg1 = CircleConstructor.main.preferences.getInteger("taskArgument1");
                int arg2 =  CircleConstructor.main.preferences.getInteger("taskArgument2");
                int reward = CircleConstructor.main.preferences.getInteger("taskReward");
                currentTask = new TaskGame(arg1, arg2, taskWord[taskIndex][0], taskWord[taskIndex][1]);
                currentTask.setReward(reward);
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex],arg1, arg2, reward));
            }
            else if(taskWord[taskIndex].length == 2 && taskWord[taskIndex][0].equalsIgnoreCase("word")) {
                int arg1 = CircleConstructor.main.preferences.getInteger("taskArgument1");
                int reward = CircleConstructor.main.preferences.getInteger("taskReward");
                CircleConstructor.main.letterFind();
                currentTask = new TaskGame(arg1, taskWord[taskIndex][1]);
                currentTask.setReward(reward);
                currentTask.wordIndex = CircleConstructor.main.preferences.getInteger("taskWordIndex");
                currentTask.word = wordsNum[currentTask.wordIndex];
                ui.newTask(String.format(Locale.US, UI.selectedLanguage == Text.EN ? taskTextEN[taskIndex] : taskTextRU[taskIndex], "''" + words[currentTask.wordIndex] + "''", reward));
            }
            currentTask.taskIndex = taskIndex;
            timer = 10000;
        }
        else ui.withoutTask();
    }
}
