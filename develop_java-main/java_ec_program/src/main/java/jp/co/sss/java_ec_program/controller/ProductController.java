package jp.co.sss.java_ec_program.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import jp.co.sss.java_ec_program.dto.UserRegistrationDto;
import jp.co.sss.java_ec_program.entity.Carts;
import jp.co.sss.java_ec_program.entity.Companies;
import jp.co.sss.java_ec_program.entity.Order_items;
import jp.co.sss.java_ec_program.entity.Products;
import jp.co.sss.java_ec_program.entity.Reviews;
import jp.co.sss.java_ec_program.entity.Users;
import jp.co.sss.java_ec_program.repository.CartRepository;
import jp.co.sss.java_ec_program.repository.CompanyRepository;
import jp.co.sss.java_ec_program.repository.OrderItemRepository;
import jp.co.sss.java_ec_program.repository.ProductRepository;
import jp.co.sss.java_ec_program.repository.ReviewsRepository;
import jp.co.sss.java_ec_program.repository.UserRepository;
import jp.co.sss.java_ec_program.session.UserSession;

@Controller
@RequestMapping("/views")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSession userSession;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    // 商品詳細画面
    @GetMapping("/product_detail/{productId}")
    public String getProductDetails(@PathVariable Long productId, Model model) {
        Optional<Products> productOpt = productRepository.findById(productId);

            Products product = productOpt.get();
            Optional<Companies> companyOpt = companyRepository.findById(product.getCompanyId());

            //CompanyName取得
            if (companyOpt.isPresent()) {
                model.addAttribute("companyName", companyOpt.get().getCompanyName());
            } else {
                model.addAttribute("companyName", "Unknown Company");
            }

            List<Reviews> reviews = reviewsRepository.findByProductId(product.getProduct_id());

            //レビューの日付を型に合わせる
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<String> formattedDates = reviews.stream()
                                                 .map(review -> review.getCreatedAt().format(formatter))
                                                 .collect(Collectors.toList());
            model.addAttribute("formattedDates", formattedDates);
            model.addAttribute("reviews", reviews);
            model.addAttribute("product", product);
            return "views/product_detail";

    }

    // ユーザー登録画面
    @GetMapping("/users/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "views/users/register";
    }

    @PostMapping("/users/register")
    public String registerUser(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
                               BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "views/users/register";
        }

        //パスワード確認
        if (!userDto.getPasswords().equals(userDto.getPasswordConfirmation())) {
            result.rejectValue("passwordConfirmation", null, "パスワードとパスワード（確認）が一致しません");
            return "views/users/register";
        }

        //Userを保存
        Users user = new Users();
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPasswords(userDto.getPasswords());
        userRepository.save(user);
        return "redirect:/views/users/login";
    }

    // ログイン画面
    @GetMapping("/users/login")
    public String login() {
        return "views/users/login";
    }

    @PostMapping("/users/login")
    public String loginUser(String username, String password, Model model) {
        Users user = userRepository.findByUserName(username);
        if (user != null && user.getPasswords().equals(password)) {
        	//ユーザーセッションにセット
            userSession.setUser(user);
            return "redirect:/views/product_detail/1";//仮設定で商品1の商品詳細画面へ
        } else {
            model.addAttribute("error", "ユーザー名かパスワードが間違っています");
            return "views/users/login";
        }
    }

    // 決済完了画面
    @GetMapping("/complete_order")
    public String completeOrder(@RequestParam(name = "orderId", required = false, defaultValue = "0") Long orderId, Model model) {

        List<Order_items> orderItems = orderItemRepository.findByOrderOrderId(orderId);

        	//合計金額の計算
            double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity() * (1 + (item.getProduct().getIncludeTax() / 100.0)))
                .sum();

            model.addAttribute("orderId", orderId);
            model.addAttribute("orderItems", orderItems);
            model.addAttribute("totalAmount", totalAmount);

            return "views/complete_order";

    }

    // カート追加画面
    @PostMapping("/add_to_cart")
    public String addToCart(@RequestParam Long productId, @RequestParam Integer quantity, Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        //Userに紐づけてcartテーブルに保存
        Users currentUser = userSession.getUser();
        Optional<Products> productOpt = productRepository.findById(productId);

            Products product = productOpt.get();

            Carts cartItem = new Carts();
            cartItem.setUser(currentUser);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartRepository.save(cartItem);

            model.addAttribute("product", product);
            model.addAttribute("quantity", quantity);

            return "views/add_to_cart";

    }

    // カート内詳細画面
    @GetMapping("/cart_detail")
    public String getCartDetails(Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        Users currentUser = userSession.getUser();
        List<Carts> cartItems = cartRepository.findByUser(currentUser);

        //合計金額を計算
        int totalAmount = cartItems.stream()
            .mapToInt(cart -> (int) (cart.getProduct().getPrice() * cart.getQuantity() * (1 + (cart.getProduct().getIncludeTax() / 100.0))))
            .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "views/cart_detail";
    }

    // 決済完了画面へのテスト用
    @GetMapping("/checkout")
    public String checkout(@RequestParam(name = "orderId", required = false, defaultValue = "1") Long orderId) {
        return "redirect:/views/complete_order?orderId=" + orderId;
    }
}