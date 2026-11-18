package tacos.web;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.jspecify.annotations.NonNull;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import tacos.TacoOrder;
import tacos.data.OrderRepository;

@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {

  private OrderRepository orderRepo;

  public OrderController(OrderRepository orderRepo) {
    this.orderRepo = orderRepo;
  }

  // ถ้ายังไม่มี tacoOrder ใน session (เช่น เพิ่งรีสตาร์ทแอป) ให้สร้าง order เปล่าขึ้นมาใหม่
  // ถ้ามีอยู่แล้ว Spring จะใช้ตัวใน session และไม่เรียก method นี้
  @ModelAttribute(name = "tacoOrder")
  public TacoOrder order() {
    return new TacoOrder();
  }

  @GetMapping("/current")
  public String orderForm(@ModelAttribute("tacoOrder") TacoOrder tacoOrder) {
    // ยังไม่มี taco ใน order (เช่น เข้าหน้านี้ตรงๆ หรือเพิ่งรีสตาร์ทแอป) -> กลับไปออกแบบ taco ก่อน
    if (tacoOrder.getTacos().isEmpty()) {
      return "redirect:/design";
    }
    return "orderForm";
  }

  @PostMapping
  public String processOrder(@Valid @NonNull TacoOrder order, Errors errors, SessionStatus sessionStatus) {
    if (errors.hasErrors()) {
      return "orderForm";
    }

    orderRepo.save(order);
    sessionStatus.setComplete();

    return "redirect:/";
  }

}
