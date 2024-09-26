create table account
(
    account_id varchar(255) not null
        primary key,
    address    varchar(200) null,
    avatar     varchar(255) null,
    dob        date         null,
    email      varchar(150) null,
    full_name  varchar(100) null,
    password   varchar(255) null,
    phone      varchar(20)  null,
    user_name  varchar(100) null
)
    engine = InnoDB;

create table cart
(
    cart_id    varchar(255)   not null
        primary key,
    price      decimal(38, 2) null,
    quantity   int            null,
    account_id varchar(255)   null,
    product_id varchar(255)   null,
    combo_id   varchar(255)   null,
    constraint FK4pljlvncf45mr98etwpubxvbt
        foreign key (account_id) references account (account_id)
)
    engine = InnoDB;

create index FK3d704slv66tw6x5hmbm6p2x3u
    on cart (product_id);

create index FKj0q80msu17dbn881jgskx8yqp
    on cart (combo_id);

create table category
(
    category_id   varchar(255) not null
        primary key,
    category_name varchar(100) null,
    created_date  date         null,
    status        int          null,
    updated_date  date         null
)
    engine = InnoDB;

create table combo
(
    combo_id     varchar(255)   not null
        primary key,
    combo_name   varchar(100)   null,
    created_date date           null,
    end_date     date           null,
    final_amount decimal(38, 2) null,
    start_date   date           null,
    status       int            null,
    total_amount decimal(38, 2) null,
    updated_date date           null,
    image        varchar(255)   null,
    description  varchar(255)   null
)
    engine = InnoDB;

create definer = java@`%` trigger before_update_combo
    before update
                      on combo
                      for each row
BEGIN
  IF NEW.end_date < NOW() AND NEW.status != 1 THEN
    SET NEW.status = 1;
END IF;
END;

create table event
(
    event_id    varchar(255)  not null
        primary key,
    content     varchar(4000) null,
    event_name  varchar(255)  null,
    image       varchar(255)  null,
    description varchar(4000) null
)
    engine = InnoDB;

create table orders
(
    order_id         varchar(255)   not null
        primary key,
    order_date       datetime(6)    null,
    status           tinyint        null,
    total_amount     decimal(38, 2) null,
    account_id       varchar(255)   null,
    shipping_address varchar(255)   null,
    shipping_phone   varchar(20)    null,
    code             varchar(8)     null,
    email            varchar(150)   null,
    first_name       varchar(100)   null,
    last_name        varchar(100)   null,
    constraint FK3c7gbsfawn58r27cf5b2km72f
        foreign key (account_id) references account (account_id)
)
    engine = InnoDB;

create table invoice
(
    invoice_id          varchar(255)   not null
        primary key,
    invoice_date        datetime(6)    null,
    payment_method      varchar(255)   null,
    payment_status      tinyint        null,
    total_amount        decimal(38, 2) null,
    order_id            varchar(255)   null,
    payment_date        datetime(6)    null,
    bank_code           varchar(255)   null,
    bank_transaction_no varchar(255)   null,
    card_type           varchar(255)   null,
    transaction_no      varchar(255)   null,
    transaction_status  varchar(255)   null,
    constraint UKgnfabg6rvhoc3c9o4deqb1hn4
        unique (order_id),
    constraint FKthf5w8xuexpjinfl7xheakhqn
        foreign key (order_id) references orders (order_id)
)
    engine = InnoDB;

create table invoice_detail
(
    invoice_detail_id varchar(255)   not null
        primary key,
    price             decimal(38, 2) null,
    product_id        varchar(255)   null,
    quantity          int            null,
    invoice_id        varchar(255)   null,
    total_price       decimal(38, 2) null,
    combo_id          varchar(255)   null,
    constraint FKit1rbx4thcr6gx6bm3gxub3y4
        foreign key (invoice_id) references invoice (invoice_id)
)
    engine = InnoDB;

create table product
(
    product_id   varchar(255)   not null
        primary key,
    created_date date           null,
    description  tinytext       null,
    image        varchar(255)   null,
    price        decimal(38, 2) null,
    product_name varchar(100)   null,
    status       int            null,
    updated_date date           null,
    category_id  varchar(255)   null,
    constraint FK1mtsbur82frn64de7balymq9s
        foreign key (category_id) references category (category_id)
)
    engine = InnoDB;

create table combo_detail
(
    quantity     int            null,
    unique_price decimal(38, 2) null,
    combo_id     varchar(255)   not null,
    product_id   varchar(255)   not null,
    primary key (combo_id, product_id),
    constraint FKlnuk800ydl5vmednpqv6g75uc
        foreign key (product_id) references product (product_id),
    constraint FKs943yxk1fctp436ptss9kn5sl
        foreign key (combo_id) references combo (combo_id)
)
    engine = InnoDB;

create table order_detail
(
    order_detail_id varchar(255)   not null
        primary key,
    price           decimal(38, 2) null,
    quantity        int            null,
    total_price     decimal(38, 2) null,
    combo_id        varchar(255)   null,
    order_id        varchar(255)   null,
    product_id      varchar(255)   null,
    constraint FKb8bg2bkty0oksa3wiq5mp5qnc
        foreign key (product_id) references product (product_id),
    constraint FKrws2q0si6oyd6il8gqe2aennc
        foreign key (order_id) references orders (order_id),
    constraint FKtlelomj1ok21jglgntfr48j2q
        foreign key (combo_id) references combo (combo_id)
)
    engine = InnoDB;

create table role
(
    role_id   varchar(255) not null
        primary key,
    role_name varchar(255) null
)
    engine = InnoDB;

create table accounts_roles
(
    account_id varchar(255) not null,
    role_id    varchar(255) not null,
    primary key (account_id, role_id),
    constraint FK5cibrbfgsejib0fw59m2o59dx
        foreign key (account_id) references account (account_id),
    constraint FKocid74jdgib9xk7u5tdmqxmff
        foreign key (role_id) references role (role_id)
)
    engine = InnoDB;

create definer = java@`%` event update_combo_status on schedule
    every '1' HOUR
        starts '2024-08-01 10:21:38'
    enable
    do
UPDATE combo
SET status = 1
WHERE end_date < NOW() AND status != 1;

