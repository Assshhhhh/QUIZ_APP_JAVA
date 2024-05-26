package com.example.quiz_app.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.quiz_app.models.Category;
import com.example.quiz_app.models.Question;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyQuiz.db";
    private static final int DATABASE_VERSION = 1;

    public static final String CAT_TABLE_NAME = "quiz_categories";
    public static final String COLUMN_CAT_ID = "cat_id";
    public static final String COLUMN_CAT_NAME = "name";

    public static final String TABLE_NAME = "quiz_questions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_OPTION1 = "option1";
    public static final String COLUMN_OPTION2 = "option2";
    public static final String COLUMN_OPTION3 = "option3";
    public static final String COLUMN_OPTION4 = "option4";
    public static final String COLUMN_ANSWER_NO = "answer_no";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_CATEGORY_ID = "category_id";

    private SQLiteDatabase db;
    private static QuizDbHelper instance;

    private QuizDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);

    }

    public static synchronized QuizDbHelper getInstance(Context context){
        if (instance == null){
            instance = new QuizDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CAT_TABLE_NAME +
                        "(" + COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_CAT_NAME + " TEXT  " + ");";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        COLUMN_QUESTION + " TEXT, " + COLUMN_OPTION1 + " TEXT, " +
                        COLUMN_OPTION2 + " TEXT, " + COLUMN_OPTION3 + " TEXT, " +
                        COLUMN_OPTION4 + " TEXT, " + COLUMN_ANSWER_NO + " INTEGER, " +
                        COLUMN_DIFFICULTY + " TEXT, " + COLUMN_CATEGORY_ID + " INTEGER, " +
                        "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " +
                        CAT_TABLE_NAME + "(" + COLUMN_CAT_ID + ")" + "ON DELETE CASCADE" +
                        ");";

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillCategoriesTable();
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CAT_TABLE_NAME);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable(){
        Category c1 = new Category("Programming");
        addCategory(c1);
        Category c2 = new Category("Geography");
        addCategory(c2);
        Category c3 = new Category("Math");
        addCategory(c3);
    }

    private void addCategory(Category category){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CAT_NAME,category.getName());
        db.insert(CAT_TABLE_NAME,null,cv);
    }

    private void fillQuestionsTable(){
        //Programming Questions
            easyPro();
            mediumPro();
            hardPro();
        //Geography Questins
            easyGeo();
            mediumGeo();
            hardGeo();
        //Math Questions
            easyMath();
            mediumMath();
            hardMath();

    }
    private void addQuestion(Question question){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUESTION,question.getQuestion());
        cv.put(COLUMN_OPTION1,question.getOption1());
        cv.put(COLUMN_OPTION2,question.getOption2());
        cv.put(COLUMN_OPTION3,question.getOption3());
        cv.put(COLUMN_OPTION4,question.getOption4());
        cv.put(COLUMN_ANSWER_NO,question.getAnswer());
        cv.put(COLUMN_DIFFICULTY,question.getDifficulty());
        cv.put(COLUMN_CATEGORY_ID,question.getCategoryID());
        db.insert(TABLE_NAME,null,cv);
    }


    @SuppressLint("Range")
    public List<Category> getAllCategories(){
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CAT_TABLE_NAME, null);

        if (cursor.moveToFirst()){
            do{
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CAT_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CAT_NAME)));
                categoryList.add(category);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }

    @SuppressLint("Range")
    public ArrayList<Question> getAllQuestions(){
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if(cursor.moveToFirst()){
            do{
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION3)));
                question.setOption4(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION4)));
                question.setAnswer(cursor.getInt(cursor.getColumnIndex(COLUMN_ANSWER_NO)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(COLUMN_DIFFICULTY)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                questionList.add(question);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    @SuppressLint("Range")
    public ArrayList<Question> getQuestions(int categoryID, String difficulty){
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = COLUMN_CATEGORY_ID + " =?" + " AND " + COLUMN_DIFFICULTY + " =?";
        String[] selectionArgs = new String[]{String.valueOf(categoryID),difficulty};
        Cursor cursor = db.query(TABLE_NAME,null,selection,selectionArgs,
                                null,null,null);

        if(cursor.moveToFirst()){
            do{
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION3)));
                question.setOption4(cursor.getString(cursor.getColumnIndex(COLUMN_OPTION4)));
                question.setAnswer(cursor.getInt(cursor.getColumnIndex(COLUMN_ANSWER_NO)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(COLUMN_DIFFICULTY)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                questionList.add(question);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    //Programming Questions
    private void easyPro(){
        Question q1 = new Question("What is another term for a mistake in a computer program?",
                "Bit","Bug","Botch","Byte",2,
                Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        addQuestion(q1);
        Question q2 = new Question("A computer program is best described as a series of ___________.",
                "Instructions","Questions","Answers","Numbers",1,
                Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        addQuestion(q2);
        Question q3 = new Question("Functions are of ______ types.",
                "0","1","2","3",3,
                Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        addQuestion(q3);
        Question q4 = new Question("Which of the following are the popular functional programming languages?",
                "Java","Python","C++","All of the above",4,
                Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        addQuestion(q4);
        Question q5 = new Question("In C programming language, which of the following type of operators have the highest precedence",
                "Relational Operators","Logical Operators","Equality Operators","Arithmetic Operators",4,
                Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        addQuestion(q5);

    }
    private void mediumPro(){
        Question q1 = new Question("A conditional statement in plain English usually contains which pair of words?","If, then","And, or","Either, neither","But, also",1,
                Question.DIFFICULTY_MEDIUM,Category.PROGRAMMING);
        addQuestion(q1);
        Question q2 = new Question("Imagine a car as a computer, and its engine as a program. Which of the following is an input for the engine program?",
                "gas pedal","steering wheel","driver's seat","windshield wipers",1,
                Question.DIFFICULTY_MEDIUM,Category.PROGRAMMING);
        addQuestion(q2);
        Question q3 = new Question("A ________ is similar to function prototype in which number of parameters datatype of parameters & order of appearance should be in similar order.",
                "function prototype","control function","function signature","flow function",3,
                Question.DIFFICULTY_MEDIUM,Category.PROGRAMMING);
        addQuestion(q3);
        Question q4 = new Question("Which of the following are advantages of Functional Programming?",
                "Bugs-Free code","Efficient Parallel Programming","Efficiency","All of the above",4,
                Question.DIFFICULTY_MEDIUM,Category.PROGRAMMING);
        addQuestion(q4);
        Question q5 = new Question("In Call by Value method the original value ________ be changed.",
                "can","cannot","depends on function","depends on language",2,
                Question.DIFFICULTY_MEDIUM,Category.PROGRAMMING);
        addQuestion(q5);
    }
    private void hardPro(){
        Question q1 = new Question("A ____ is a mini-program with several lines that can be called on whenever needed.",
                "Code","Function","Bug","Loop",2,
                Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestion(q1);
        Question q2 = new Question("Which terms best describes each step in a computer program?",
                "Vague","Confusing","Random","Specific",4,
                Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestion(q2);
        Question q3 = new Question("Sequence means","order of events","out of order","move around","go left",1,
                Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestion(q3);
        Question q4 = new Question("A compound statement or block is enclosed in a pair of _____.",
                "\"\"","{}","()","[]",2,
                Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestion(q4);
        Question q5 = new Question("________ is possible when a derived class function has the same name and signature as its base class.",
                "Function overriding","Function overloading","Both","None",1,
                Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestion(q5);
    }

    //Geography Questions
    private void easyGeo(){
        Question q1 = new Question("Australia’s longest river system is made by__?",
                "Darling River","Murray River","Culgoa River","Paroo River",1,
                Question.DIFFICULTY_EASY,Category.GEOGRAPHY);
        addQuestion(q1);
        Question q2 = new Question("Lake Winnipeg is located in which of the following countries?",
                "Brazil","Canada","Russia","Japan",2,
                Question.DIFFICULTY_EASY,Category.GEOGRAPHY);
        addQuestion(q2);
        Question q3 = new Question("Ganymede is the largest moon in the Solar System. It is a satellite of ________?",
                "Venus","Saturn","Jupiter","Mars",3,
                Question.DIFFICULTY_EASY,Category.GEOGRAPHY);
        addQuestion(q3);
        Question q4 = new Question("Which of the following country is known as Kalaallit Nunaat, meaning “Land of the people”?",
                "Estonia","Greenland","Iceland","Panama",2,
                Question.DIFFICULTY_EASY,Category.GEOGRAPHY);
        addQuestion(q4);
        Question q5 = new Question("Which of the following country is the largest producer of Uranium?",
                "Canada","Australia","Russia","France",1,
                Question.DIFFICULTY_EASY,Category.GEOGRAPHY);
        addQuestion(q5);

    }
    private void mediumGeo(){
        Question q1 = new Question("The Archipelago Sea is the part of which sea?",
                "Arabian Sea","Mediterranean Sea","Baltic Sea","Sea of Japan",3,
                Question.DIFFICULTY_MEDIUM,Category.GEOGRAPHY);
        addQuestion(q1);
        Question q2 = new Question("Which is the biggest island in the Indian Ocean?",
                "Madagascar","Greenland","Sumatra","Baffin Island",1,
                Question.DIFFICULTY_MEDIUM,Category.GEOGRAPHY);
        addQuestion(q2);
        Question q3 = new Question("Who among the following discovered the largest moon of  Saturn – Titan?",
                "Galileo","Christiaan Huygens","Nicholas Copernicus","F. Hoyle",2,
                Question.DIFFICULTY_MEDIUM,Category.GEOGRAPHY);
        addQuestion(q3);
        Question q4 = new Question("The phenomena ‘Great white spot’ is associated with which planet?",
                "Jupiter","Uranus","Neptune","Saturn",4,
                Question.DIFFICULTY_MEDIUM,Category.GEOGRAPHY);
        addQuestion(q4);
        Question q5 = new Question("What is the landfrom where deposits of fine silts and clay deposit after a flood called?",
                "Backswamp","Delta","Moraine","Mesa",1,
                Question.DIFFICULTY_MEDIUM,Category.GEOGRAPHY);
        addQuestion(q5);
    }
    private void hardGeo(){
        Question q1 = new Question("Which island is located geographically in America but politically in Europe?",
                "Alexander Island","Bathurst Island","Christmas Island","Greenland",4,
                Question.DIFFICULTY_HARD,Category.GEOGRAPHY);
        addQuestion(q1);
        Question q2 = new Question("What are reddish loops of gas that link parts of sunspot regions called?",
                "Solar flare","Prominence","Solar wind","Sunspot",2,
                Question.DIFFICULTY_HARD,Category.GEOGRAPHY);
        addQuestion(q2);
        Question q3 = new Question("Which among the following is not an Eon?",
                "Archean","Hadean","Palaeozoic","Proterozoic",3,
                Question.DIFFICULTY_HARD,Category.GEOGRAPHY);
        addQuestion(q3);
        Question q4 = new Question("Which among the following is not a Major plate?",
                "Australian plate","African plate","Carribean plate","South American plate",3,
                Question.DIFFICULTY_HARD,Category.GEOGRAPHY);
        addQuestion(q4);
        Question q5 = new Question("Which is the world’s largest saline inland sea?",
                "Aral Sea","Mediterranean Sea","Black Sea","Caspian Sea",4,
                Question.DIFFICULTY_HARD,Category.GEOGRAPHY);
        addQuestion(q5);
    }

    //Math Questions
    private void easyMath(){
        Question q1 = new Question("Perimeter of a square =",
                "4 × Length of a side","2 × Length of a side","3 × Length of a side","6 × Length of a side",1,
                Question.DIFFICULTY_EASY,Category.MATH);
        addQuestion(q1);
        Question q2 = new Question("The conjunction of the statement:\"Freezing point of water is 0°\" , \"Boiling point is 100° \"",
                "Freezing point of water is 0° or Boiling point of water is 100°",
                "Freezing point of water is 0° and Boiling point of water is 100°",
                "Freezing point of water is 0° else Boiling point of water is 100°",
                "Freezing point of water is 0° only if Boiling point of water is 100°",2,
                Question.DIFFICULTY_EASY,Category.MATH);
        addQuestion(q2);
        Question q3 = new Question("HCF of 24, 36 and 92 is:",
                "24","36","12","4",4,
                Question.DIFFICULTY_EASY,Category.MATH);
        addQuestion(q3);
        Question q4 = new Question("How many three digit numbers are there in all?",
                "102","99","900","100",3,
                Question.DIFFICULTY_EASY,Category.MATH);
        addQuestion(q4);
        Question q5 = new Question("..... numbers have terminating and non- terminating repeating decimals.",
                "Integers","whole","irrational","rational",4,
                Question.DIFFICULTY_EASY,Category.MATH);
        addQuestion(q5);
    }
    private void mediumMath(){
        Question q1 = new Question("What is the sum of the first 40 even positive integers?",
                "1600","1200","1640","1560",3,
                Question.DIFFICULTY_MEDIUM,Category.MATH);
        addQuestion(q1);
        Question q2 = new Question("A box contains 90 discs which are numbered from 1 to 90. If one disc is drawn at random from the box, the probability that it bears a perfect square number is:",
                "1/19","1/11","3/10","1/10",4,
                Question.DIFFICULTY_MEDIUM,Category.MATH);
        addQuestion(q2);
        Question q3 = new Question("If ratio test <1, then the series is:",
                "divergent","convergent","congruent","oscillatory",2,
                Question.DIFFICULTY_MEDIUM,Category.MATH);
        addQuestion(q3);
        Question q4 = new Question("Evaluate the probability of getting six on third dice, if 4 and 5 is on first two dice, the dice is rolled three times",
                "36/216","1/36","1/27","1/216",4,
                Question.DIFFICULTY_MEDIUM,Category.MATH);
        addQuestion(q4);
        Question q5 = new Question(" The wages of 10 workers for a six-day week is $ 1200. What are the one day’s wages of 4 workers?",
                "$40","$36","$80","24",3,
                Question.DIFFICULTY_MEDIUM,Category.MATH);
        addQuestion(q5);
    }
    private void hardMath(){
        Question q1 = new Question("If ∆ ABC = ∆ PQR, then CA¯ corresponds to",
                "PQ¯","QR¯","RP¯","none",3,
                Question.DIFFICULTY_HARD,Category.MATH);
        addQuestion(q1);
        Question q2 = new Question("An angle of 15° is drawn using a pair of compasses and a ruler. How is it done?",
                "Bisecting 60° angle","Bisecting 60° and 120° angles",
                "Bisecting 60° and then bisecting it again","Bisecting a 60° and 180° angles.",3,
                Question.DIFFICULTY_HARD,Category.MATH);
        addQuestion(q2);
        Question q3 = new Question("The sum and the product of the zeroes of polynomial 6x² - 5 respectively are",
                "0, 6/5","0, -6/5","0, 5/6","0, -5/6",4,
                Question.DIFFICULTY_HARD,Category.MATH);
        addQuestion(q3);
        Question q4 = new Question("If S*(p,q) is the dual of the compound statement S (p, q) then S*(~p,~q) is equivalent to",
                "S(~p,~q)","~S(p,q)","~S*(q,q)","none",2,
                Question.DIFFICULTY_HARD,Category.MATH);
        addQuestion(q4);
        Question q5 = new Question("The number of solutions of the equation |x| = cos x, is",
                "1","2","3","0",2,
                Question.DIFFICULTY_HARD,Category.MATH);
        addQuestion(q5);
    }


}
