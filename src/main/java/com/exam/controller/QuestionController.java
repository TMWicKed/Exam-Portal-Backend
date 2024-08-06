package com.exam.controller;

import com.exam.model.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.exam.model.exam.Question;
import com.exam.model.exam.Quiz;
import com.exam.service.QuestionService;
import com.exam.service.QuizService;
import com.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/question")
public class QuestionController {

//    private User user;

    private String currentQuizTitle; // **New field to store the current quiz title**
    private String currentQuizMaxMarks; // **New field to store the current quiz max marks**


    double marksGot1 = 0;
    int correctAnswers1 = 0;
    int attempted1 = 0;
    String username = "";
    LocalDateTime currentDateTime;
    LocalDate date;
    LocalTime time;
    Integer a=0;
    String formattedTime="";
//    String username = user.getUsername();

    @Autowired
    private QuestionService service;

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService; // Add this field to inject the UserService

    //add question
    @PostMapping("/")
    public ResponseEntity<Question> add(@RequestBody Question question) {
        return ResponseEntity.ok(this.service.addQuestion(question));
    }

    //update the question
    @PutMapping("/")
    public ResponseEntity<Question> update(@RequestBody Question question) {
        return ResponseEntity.ok(this.service.updateQuestion(question));
    }

    //get all question of any quid
    @GetMapping("/quiz/{qid}")
    public ResponseEntity<?> getQuestionsOfQuiz(@PathVariable("qid") Long qid) {
//        Quiz quiz = new Quiz();
//        quiz.setqId(qid);
//        Set<Question> questionsOfQuiz = this.service.getQuestionsOfQuiz(quiz);
//        return ResponseEntity.ok(questionsOfQuiz);

        Quiz quiz = this.quizService.getQuiz(qid);
        Set<Question> questions = quiz.getQuestions();
        List list = new ArrayList(questions);
        if (list.size() > Integer.parseInt(quiz.getNumberOfQuestions())) {
            list = list.subList(0, Integer.parseInt(quiz.getNumberOfQuestions() + 1));
        }
        Collections.shuffle(list);
        return ResponseEntity.ok(list);
    }


    @GetMapping("/quiz/all/{qid}")
    public ResponseEntity<?> getQuestionsOfQuizAdmin(@PathVariable("qid") Long qid) {
        Quiz quiz = new Quiz();
        quiz.setqId(qid);
        Set<Question> questionsOfQuiz = this.service.getQuestionsOfQuiz(quiz);
        return ResponseEntity.ok(questionsOfQuiz);
    }


    //get single question
    @GetMapping("/{quesId}")
    public Question get(@PathVariable("quesId") Long quesId) {
        return this.service.getQuestion(quesId);
    }

    //delete question
    @DeleteMapping("/{quesId}")
    public void delete(@PathVariable("quesId") Long quesId) {
        this.service.deleteQuestion(quesId);
    }


    //eval quiz
    @PostMapping("/eval-quiz")
    public ResponseEntity<?> evalQuiz(@RequestBody List<Question> questions) {
        System.out.println(questions);
        double marksGot = 0;
        int correctAnswers = 0;
        int attempted = 0;

        //Assuming all questions are from the same quiz
        Quiz quiz = questions.get(0).getQuiz();
        currentQuizTitle = quiz.getTitle(); //Set the current quiz title
        currentQuizMaxMarks = quiz.getMaxMarks();

        for (Question q : questions) {
            //single questions
            Question question = this.service.get(q.getQuesId());
            if (question.getAnswer().equals(q.getGivenAnswer())) {
                //correct
                correctAnswers++;

                double marksSingle = Double.parseDouble(questions.get(0).getQuiz().getMaxMarks()) / questions.size();
                //this.questions[0].quiz.maxMarks / this.questions.length;
                marksGot += marksSingle;

            }

            if (q.getGivenAnswer() != null) {
                attempted++;
            }
        }
        ;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();

        currentDateTime = LocalDateTime.now();
        date = currentDateTime.toLocalDate();
        time = currentDateTime.toLocalTime();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        formattedTime = time.format(timeFormatter);

        marksGot1=marksGot;
        correctAnswers1=correctAnswers;
        attempted1=attempted;
        System.out.println(marksGot1+" "+correctAnswers1+" "+attempted1+" "+username+" "+currentQuizTitle+" "+currentQuizMaxMarks);


        a++;
        Map<String, Object> map = Map.of("marksGot", marksGot, "correctAnswers", correctAnswers, "attempted", attempted);
        return ResponseEntity.ok(map);

    }

    @GetMapping("/sendToAdmin")
    public ResponseEntity<?> sendToAdmin() {
        //Get the currently authenticated user
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName(); //Retrieve the username of the current user

//        System.out.println(marksGot1 + " " + correctAnswers1 + " " + attempted1);
        //Include username, quiz title, and quiz max marks in the response map
        Map<String, Object> map = Map.of(
                "username", username,
                "quizTitle", currentQuizTitle,
                "quizMaxMarks", currentQuizMaxMarks, //Include the quiz max marks
                "marksGot1", marksGot1,
                "correctAnswers1", correctAnswers1,
                "attempted1", attempted1,
                "Date", date,
                "Time", formattedTime,
                "NumberOfAttemps",a
        );
        return ResponseEntity.ok(map);
    }
}
