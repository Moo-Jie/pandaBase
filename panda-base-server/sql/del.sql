use pandabasedb;

-- 开启事务，确保操作原子性
START TRANSACTION;

-- 1. 先删除用户实体商品表（依赖订单和兑换记录）
DELETE FROM user_physical_product;

-- 2. 删除订单明细表（依赖订单表）
DELETE FROM order_item;

-- 3. 删除兑换记录表（依赖兑换码表和会员卡表）
DELETE FROM redemption_record;

-- 4. 删除会员卡表（依赖订单表和兑换记录表）
DELETE FROM membership_card;

-- 5. 删除购买订单表
DELETE FROM purchase_order;

-- 6. 最后删除兑换码表
DELETE FROM redemption_code;

-- 提交事务
COMMIT;