package com.example.controller.admin;

import com.example.service.FeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    private final FeedbackService feedbackService;
    public AdminFeedbackController(FeedbackService feedbackService){
        this.feedbackService = feedbackService;
    }

    // Danh sách + filter
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer rating,
                       Model model){
        model.addAttribute("feedbackList",
                feedbackService.search(keyword, rating));
        model.addAttribute("keyword", keyword);
        model.addAttribute("rating",  rating);
        return "admin/feedback-list";
    }

    // Trang trả lời
    @GetMapping("/reply/{id}")
        public String replyForm(@PathVariable Integer id, Model model) {
            model.addAttribute("feedback",
                feedbackService.getById(id).orElseThrow());
            return "admin/feedback-reply";
        }


    @PostMapping("/reply/{id}")
    public String doReply(@PathVariable Integer id,
                          @RequestParam String reply){
        feedbackService.reply(id, reply);
        return "redirect:/admin/feedback?success=Đã+phản+hồi";
    }
}
