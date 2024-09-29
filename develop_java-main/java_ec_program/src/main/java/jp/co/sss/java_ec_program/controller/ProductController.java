package jp.co.sss.java_ec_program.controller;

import java.time.LocalDateTime;
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

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jp.co.sss.java_ec_program.dto.UserRegistrationDto;
import jp.co.sss.java_ec_program.entity.Carts;
import jp.co.sss.java_ec_program.entity.Categories;
import jp.co.sss.java_ec_program.entity.Companies;
import jp.co.sss.java_ec_program.entity.Order_items;
import jp.co.sss.java_ec_program.entity.Orders;
import jp.co.sss.java_ec_program.entity.Products;
import jp.co.sss.java_ec_program.entity.Reviews;
import jp.co.sss.java_ec_program.entity.Users;
import jp.co.sss.java_ec_program.repository.CartRepository;
import jp.co.sss.java_ec_program.repository.CategoryRepository;
import jp.co.sss.java_ec_program.repository.CompanyRepository;
import jp.co.sss.java_ec_program.repository.OrderItemRepository;
import jp.co.sss.java_ec_program.repository.OrderRepository;
import jp.co.sss.java_ec_program.repository.ProductRepository;
import jp.co.sss.java_ec_program.repository.ReviewsRepository;
import jp.co.sss.java_ec_program.repository.UserRepository;
import jp.co.sss.java_ec_program.session.UserSession;

@Controller
@RequestMapping("/views")
public class ProductController {

	// Repositories
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ReviewsRepository reviewsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserSession userSession;

    // ユーザーセッションの初期化
    @ModelAttribute("userSession")
    public UserSession userSession() {
        if (userSession.isLoggedIn()) {
            Users currentUser = userSession.getUser();
            long cartItemCount = cartRepository.countByUser(currentUser);
            userSession.setCartItemCount(cartItemCount);
        }
        return this.userSession;
    }
    
    // ユーザー登録画面
    @GetMapping("/users/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "views/users/register";
    }

    // ユーザー登録処理
    @PostMapping("/users/register")
    public String registerUser(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
                               BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "views/users/register";
        }

        // パスワード確認
        if (!userDto.getPasswords().equals(userDto.getPasswordConfirmation())) {
            result.rejectValue("passwordConfirmation", null, "パスワードとパスワード（確認）が一致しません");
            return "views/users/register";
        }

        // Userを保存
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

    // ログイン処理
    @PostMapping("/users/login")
    public String loginUser(String email, String password, Model model) {
        Users user = userRepository.findByEmail(email);
        if (user != null && user.getPasswords().equals(password)) {
            // ユーザーセッションにセット
            userSession.setUser(user);
            return "redirect:/views/top";
        } else {
            model.addAttribute("error", "ユーザー名かパスワードが間違っています");
            return "views/users/login";
        }
    }
    
    // ログアウト処理
    @GetMapping("/users/logout")
    public String logoutUser() {
        // セッションをクリア
        userSession.setUser(null);

        return "redirect:/views/users/login";
    }
    
    
    // 在庫チェックメソッド
    private boolean isStockAvailable(Products product, int quantity, Model model) {
        if (product.getStock() < quantity) {
            model.addAttribute("error", "申し訳ありません。現在、この商品の在庫が不足しています。");
            return false;
        }
        return true;
    }
    
    // トップ
    @GetMapping("/top")
    public String showTop(Model model) {
    	if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }
        List<Products> productList = productRepository.findAll();
        
        model.addAttribute("productList", productList);
        model.addAttribute("userSession", userSession);
        return "views/top";
    }
    
    // マイページ
    @GetMapping("/mypage")
    public String showMypage() {
    	if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }
        return "views/mypage";
    }
    
    // マイページ
    @PostMapping("/mypage")
    @Transactional
    public String mypage(
    		@RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
    		Model model) {
    	Users currentUser = userSession.getUser();
    	
    	// ユーザー情報を更新
        currentUser.setUserName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        userRepository.save(currentUser);
        
        return "redirect:/views/mypage";
    	
    }
    
    // 商品一覧画面
    @GetMapping("/product_list")
    public String getProductList(Model model) {
    	if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }
        List<Products> productList = productRepository.findAll();
        
        // メーカー名を取得
        List<Companies> companyList = companyRepository.findAll();
        model.addAttribute("companyList", companyList);
        model.addAttribute("productList", productList);
        model.addAttribute("userSession", userSession);
        return "views/product_list";
    }

    // カテゴリー一覧の初期化
    @ModelAttribute("categories")
    public List<Categories> populateCategories() {
        return categoryRepository.findAll();
    }

    // 商品検索機能
    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
    	if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }
        List<Products> productList = productRepository.searchByKeyword(keyword);

        // メーカー名を取得
        List<Companies> companyList = companyRepository.findAll();
        model.addAttribute("companyList", companyList);
        model.addAttribute("productList", productList);
        model.addAttribute("keyword", keyword);
        // 検索ヒットした件数を表示
		/*model.addAttribute("resultCount", productList.size());
		model.addAttribute("totalResults", productList.size());*/
        model.addAttribute("userSession", userSession);

        return "views/product_list";
    }

    // カテゴリー別商品検索機能
    @GetMapping("/searchByCategory")
    public String searchByCategory(@RequestParam("categoryId") String categoryId, Model model) {
    	if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }
        List<Products> productList;

        if ("all".equals(categoryId)) {
            // すべてのカテゴリーを選択した場合、全ての商品を取得
            productList = productRepository.findAll();
        } else {
            // 特定のカテゴリーの商品を取得
            productList = productRepository.findByCategoryId(Integer.parseInt(categoryId));
        }

        // メーカー名を取得
        List<Companies> companyList = companyRepository.findAll();
        model.addAttribute("companyList", companyList);
        model.addAttribute("productList", productList);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("userSession", userSession);
        // 検索ヒットした件数を表示
		/*model.addAttribute("resultCount", productList.size());
		model.addAttribute("totalResults", productList.size());*/
        
        return "views/product_list";
    }

    // 商品詳細画面
    @GetMapping("/product_detail/{productId}")
    public String getProductDetails(@PathVariable Long productId, Model model) {
        Optional<Products> productOpt = productRepository.findById(productId);
        Products product = productOpt.get();
        Optional<Companies> companyOpt = companyRepository.findById(product.getCompanyId());
        
        if (productOpt.isEmpty()) {
            return "redirect:/error";
        }

        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }
        // メーカー名取得
        if (companyOpt.isPresent()) {
            model.addAttribute("companyName", companyOpt.get().getCompanyName());
        } else {
            model.addAttribute("companyName", "Unknown Company");
        }

        List<Reviews> reviews = reviewsRepository.findByProductId(product.getProduct_id());

        // レビューの日付を型に合わせる
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> formattedDates = reviews.stream()
                                             .map(review -> review.getCreatedAt().format(formatter))
                                             .collect(Collectors.toList());
        model.addAttribute("formattedDates", formattedDates);
        model.addAttribute("reviews", reviews);
        model.addAttribute("product", product);
        model.addAttribute("userSession", userSession);

        return "views/product_detail";
    }

    // 口コミ投稿画面
    @GetMapping("/product_review/{productId}")
    public String showReviewForm(@PathVariable Long productId, Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }
        
        Optional<Products> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Reviews review = new Reviews();
            // 投稿者名をユーザー名で初期設定
            review.setPost(userSession.getUser().getUserName());

            model.addAttribute("product", productOpt.get());
            model.addAttribute("review", review);
            model.addAttribute("userSession", userSession);
            return "views/product_review";
        } else {
            return "redirect:/error";
        }
    }

    // 口コミを保存する処理
    @PostMapping("/product_review/{productId}")
    public String submitReview(@PathVariable Long productId, @ModelAttribute("review") Reviews review, Model model) {
        Optional<Products> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            review.setProductId(productId.intValue());
            review.setUserId(userSession.getUser().getUser_id());
            review.setCreatedAt(LocalDateTime.now());
            reviewsRepository.save(review);
            return "redirect:/views/product_list";
        } else {
        	return "redirect:/error";
        }
    }

    // カート追加処理
    @PostMapping("/add_to_cart")
    public String addToCart(@RequestParam Long productId, @RequestParam Integer quantity, Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        Users currentUser = userSession.getUser();
        Optional<Products> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Products product = productOpt.get();

            // カート内の同一商品の既存数量を取得
            Optional<Carts> existingCartItemOpt = cartRepository.findByUserAndProduct(currentUser, product);
            int existingQuantity = existingCartItemOpt.map(Carts::getQuantity).orElse(0);

            // 合計数量を計算
            int totalQuantity = existingQuantity + quantity;

            // 在庫チェック
            if (!isStockAvailable(product, totalQuantity, model)) {
                model.addAttribute("product", product);
                return "views/product_detail";
            }

            // カートに保存
            Carts cartItem = existingCartItemOpt.orElse(new Carts());
            cartItem.setUser(currentUser);
            cartItem.setProduct(product);
            cartItem.setQuantity(totalQuantity);
            cartRepository.save(cartItem);

            // カートアイテム数の再計算
            long cartItemCount = cartRepository.countByUser(currentUser);
            userSession.setCartItemCount(cartItemCount);

            model.addAttribute("product", product);
            model.addAttribute("quantity", quantity);

            return "views/add_to_cart";
        } else {
        	return "redirect:/error";
        }
    }
    
    // 合計金額を計算するメソッド
    private double calculateTotalAmount(List<Carts> cartItems) {
        return cartItems.stream()
            .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity() * (1 + (cart.getProduct().getIncludeTax() / 100.0)))
            .sum();
    }
    
    // 合計金額（税抜き）を計算するメソッド
    private double calculateTotalAmountNotIncludeTax(List<Carts> cartItems) {
        return cartItems.stream()
            .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity())
            .sum();
    }

    // カート内詳細画面
    @GetMapping("/cart_detail")
    public String getCartDetails(Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        Users currentUser = userSession.getUser();
        List<Carts> cartItems = cartRepository.findByUser(currentUser);

        // 合計金額を計算
        double totalAmount = calculateTotalAmount(cartItems);
        double totalAmountNotIncludeTax = calculateTotalAmountNotIncludeTax(cartItems);

        // カートが空の場合はエラーメッセージ
        if (cartItems.isEmpty()) {
            model.addAttribute("isCartEmpty", true);
            return "views/cart_detail";
        } else {
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("totalAmountNotIncludeTax", totalAmountNotIncludeTax);
            
            // カートアイテム数を計算
            long cartItemCount = cartItems.stream().mapToLong(Carts::getQuantity).sum();
            model.addAttribute("cartItemCount", cartItemCount);
        }

        model.addAttribute("userSession", userSession);
        return "views/cart_detail";
    }
    
    // カート内商品の消去
    @PostMapping("/delete_cart_item/{cartItemId}")
    public String deleteCartItem(@PathVariable Long cartItemId) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        cartRepository.deleteById(cartItemId);

        // カートアイテム数の再計算
        Users currentUser = userSession.getUser();
        long cartItemCount = cartRepository.countByUser(currentUser);
        userSession.setCartItemCount(cartItemCount);

        return "redirect:/views/cart_detail";
    }
    
    // 購入商品詳細画面
    @GetMapping("/purchase_detail")
    public String showPurchaseDetail(
            @RequestParam(value = "productId", required = false) Long productId,
            Model model) {
    	if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        if (productId != null) {
            // シナリオ1: 単品購入の場合、数量は1に固定
            Optional<Products> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Products product = productOpt.get();

                // 在庫チェック
                if (!isStockAvailable(product, 1, model)) {
                    model.addAttribute("product", product);
                    return "views/product_detail";
                }

                model.addAttribute("singleProduct", product);
                model.addAttribute("quantity", 1);// 数量は1固定

                // 合計金額の計算
                double totalAmount = product.getPrice() * 1 * (1 + (product.getIncludeTax() / 100.0));
                model.addAttribute("totalAmount", totalAmount);
            } else {
            	return "redirect:/error";
            }
        } else {
            // シナリオ2: カート内全商品の購入の場合
            Users currentUser = userSession.getUser();
            List<Carts> cartItems = cartRepository.findByUser(currentUser);
            // カートが空の場合はエラーメッセージ
            if (cartItems.isEmpty()) {
                model.addAttribute("isCartEmpty", true);
                return "views/purchase_detail";
            } else {
                model.addAttribute("isCartEmpty", false);
                model.addAttribute("cartItems", cartItems);
            }

            // 合計金額の計算
            double totalAmount = calculateTotalAmount(cartItems);
            model.addAttribute("totalAmount", totalAmount);
        }

        model.addAttribute("userSession", userSession);
        return "views/purchase_detail";
    }
    
    @PostMapping("/purchase_detail")
    public String updateQuantityOrConfirmPurchase(
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "cartItemId", required = false) Long cartItemId,
            @RequestParam("quantity") Integer quantity,
            Model model) {

        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        // 数量が無効の場合エラーメッセージ
        if (quantity == null || quantity < 1) {
            model.addAttribute("error", "数量が無効です");
            return "views/purchase_detail";
        }

        if (productId != null) {
            // シナリオ1: 単品購入の場合
            Optional<Products> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Products product = productOpt.get();

                // 在庫チェック
                if (!isStockAvailable(product, quantity, model)) {
                    model.addAttribute("product", product);
                    return "views/product_detail";
                }

                // 単品購入の数量を更新して表示
                model.addAttribute("singleProduct", product);
                model.addAttribute("quantity", quantity);

                // 合計金額の計算
                double totalAmount = product.getPrice() * quantity * (1 + (product.getIncludeTax() / 100.0));
                model.addAttribute("totalAmount", totalAmount);
            } else {
                return "redirect:/error";
            }
        } else if (cartItemId != null) {
            // シナリオ2: カート内商品の場合
            Optional<Carts> cartOpt = cartRepository.findById(cartItemId);
            if (cartOpt.isPresent()) {
                Carts cartItem = cartOpt.get();
                Products product = cartItem.getProduct();

                // 在庫チェック
                if (!isStockAvailable(product, quantity, model)) {
                    model.addAttribute("product", product);
                    return "views/cart_detail";
                }

                // カートアイテムの数量を更新してデータベースに保存
                cartItem.setQuantity(quantity);
                cartRepository.save(cartItem);

                // 最新のカートアイテムを取得して表示
                Users currentUser = userSession.getUser();
                List<Carts> cartItems = cartRepository.findByUser(currentUser);
                model.addAttribute("cartItems", cartItems);

                // 合計金額の計算
                double totalAmount = calculateTotalAmount(cartItems);
                model.addAttribute("totalAmount", totalAmount);
                return "redirect:/views/purchase_detail";
            } else {
                return "redirect:/error";
            }
        } else {
            return "redirect:/error";
        }

        model.addAttribute("userSession", userSession);
        return "views/purchase_detail";
    }
    
 // 購入品確認画面
    @PostMapping("/order_confirmation")
    public String confirmOrder(
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("cardInfo") String cardInfo,
            @RequestParam(value = "newCardNumber", required = false) String newCardNumber,
            @RequestParam("addressInfo") String addressInfo,
            @RequestParam(value = "newAddress", required = false) String newAddress,
            Model model) {

        Users currentUser = userSession.getUser();
        
        // ユーザー情報を更新
        currentUser.setUserName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        model.addAttribute("cardInfo", cardInfo);
        model.addAttribute("newCardNumber", newCardNumber);
        model.addAttribute("addressInfo", addressInfo);
        model.addAttribute("newAddress", newAddress);
        
        userRepository.save(currentUser);
        
        if (productId != null && quantity != null) {
            // シナリオ1: 単品購入
            Optional<Products> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Products product = productOpt.get();

                // 在庫チェック
                if (!isStockAvailable(product, quantity, model)) {
                    model.addAttribute("product", product);
                    model.addAttribute("quantity", quantity);
                    return "views/purchase_detail";
                }

                // 合計金額の計算
                double totalAmount = product.getPrice() * quantity * (1 + (product.getIncludeTax() / 100.0));
                model.addAttribute("totalAmount", totalAmount);
                model.addAttribute("singleProduct", product);
                model.addAttribute("quantity", quantity);
            } else {
            	return "redirect:/error";
            }
        } else {
            // シナリオ2: カート購入
            List<Carts> cartItems = cartRepository.findByUser(currentUser);

            // 在庫チェック
            for (Carts cartItem : cartItems) {
                Products product = cartItem.getProduct();
                if (!isStockAvailable(product, cartItem.getQuantity(), model)) {
                    model.addAttribute("cartItems", cartItems);
                    return "views/cart_detail";
                }
            }

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("isCartEmpty", false);
            // 合計金額の計算
            double totalAmount = calculateTotalAmount(cartItems);
            model.addAttribute("totalAmount", totalAmount);
        }

        return "views/order_confirmation";
    }
    
    // 注文確認画面表示
    @GetMapping("/order_confirmation")
    public String showOrderConfirmation(Model model) {
    	if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        Users currentUser = userSession.getUser();
        List<Carts> cartItems = cartRepository.findByUser(currentUser);

        // カートが空の場合エラーメッセージ
        if (cartItems.isEmpty()) {
            model.addAttribute("isCartEmpty", true);
            return "views/order_confirmation";
        } else {
            model.addAttribute("isCartEmpty", false);
            model.addAttribute("cartItems", cartItems);
            
            // 合計金額の計算
            double totalAmount = calculateTotalAmount(cartItems);
            model.addAttribute("totalAmount", totalAmount);
        }

        model.addAttribute("userSession", userSession);
        return "views/order_confirmation";
    }

    // 決済完了画面表示
    @GetMapping("/complete_order")
    public String showCompleteOrder(@RequestParam(name = "orderId") Long orderId, Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/views/users/login";
        }

        Optional<Orders> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            model.addAttribute("error", "注文が見つかりませんでした。");
            return "redirect:/error";
        }

        List<Order_items> orderItems = orderItemRepository.findByOrderOrderId(orderId);

        // 合計金額の計算
        double totalAmount = orderItems.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity() * (1 + (item.getProductId().getIncludeTax() / 100.0)))
            .sum();

        // データをモデルに追加
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("totalAmount", totalAmount);

        return "views/complete_order";
    }

    // 注文確定処理
    @PostMapping("/complete_order")
    @Transactional
    public String processCompleteOrder(
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone) {
        Users currentUser = userSession.getUser();

        // ユーザー情報を更新
        currentUser.setUserName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        userRepository.save(currentUser);

        if (productId != null && quantity != null) {
            // シナリオ1: 単品購入
            Optional<Products> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Products product = productOpt.get();

                // 在庫チェック
                if (product.getStock() < quantity) {
                    return "redirect:/error";
                }

                // 新しい注文を追加
                Orders newOrder = new Orders();
                newOrder.setUser(currentUser);
                newOrder.setTotalAmount((int)(product.getPrice() * quantity * (1 + (product.getIncludeTax() / 100.0))));
                newOrder.setStatus("完了");
                orderRepository.save(newOrder);

                // 在庫を更新
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);

                // Order_itemsに反映
                Order_items orderItem = new Order_items();
                orderItem.setOrder(newOrder);
                orderItem.setProductId(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice());
                orderItemRepository.save(orderItem);

                // 完了画面へリダイレクト
                return "redirect:/views/complete_order?orderId=" + newOrder.getOrderId();

            } else {
            	return "redirect:/error";
            }
        } else {
            // シナリオ2: カート内商品の購入
            List<Carts> cartItems = cartRepository.findByUser(currentUser);

            if (cartItems.isEmpty()) {
                return "redirect:/error";
            }

            // 在庫チェック
            for (Carts cartItem : cartItems) {
                Products product = cartItem.getProduct();
                if (product.getStock() < cartItem.getQuantity()) {
                    return "redirect:/error";
                }
            }

            // 新しい注文を追加
            Orders newOrder = new Orders();
            newOrder.setUser(currentUser);
            newOrder.setTotalAmount(cartItems.stream()
                .mapToInt(cart -> (int)(cart.getProduct().getPrice() * cart.getQuantity() * (1 + (cart.getProduct().getIncludeTax() / 100.0))))
                .sum());
            newOrder.setStatus("完了");
            orderRepository.save(newOrder);

            // Order_itemsテーブルに反映を追加 & Productsテーブルの在庫を更新
            for (Carts cartItem : cartItems) {
                Products product = cartItem.getProduct();

                // 在庫を更新
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);

                // Order_itemsにアイテムを追加
                Order_items orderItem = new Order_items();
                orderItem.setOrder(newOrder);
                orderItem.setProductId(product);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(product.getPrice());
                orderItemRepository.save(orderItem);
            }

            // カートをクリア
            cartRepository.deleteByUser(currentUser);

            // カートアイテム数の再計算
            userSession.setCartItemCount(0);

            return "redirect:/views/complete_order?orderId=" + newOrder.getOrderId();
        }
    }

}