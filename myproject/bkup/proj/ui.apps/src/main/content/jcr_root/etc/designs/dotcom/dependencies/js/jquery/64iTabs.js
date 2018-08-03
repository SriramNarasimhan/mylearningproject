(function(n) {
    n.fn.extend({
        responsiveTabs: function() {
            var t = "tabset-active",
                o = "tabset-nav-list",
                r = "tabset-nav-list-item",
                u = "tabset-nav-list-link",
                e = "tabset-content",
                i = "tabset-content-link",
                f;
            this.each(function(s) {
                function y() {
                    f = n("." + i).is(":visible") ? "accordion" : "tabset"
                }
                var v = s,
                    c = 0,
                    h = n(this),
                    w = h.children("ul"),
                    b = w.find("li"),
                    a = b.find("a"),
                    p = h.children("div").before('<a href="#"></a>'),
                    l;
                $tabsetContentLink = h.children("a"), typeof HashSearch != "undefined" && HashSearch.keyExists("tab") && (l = HashSearch.get("tab"), Math.floor(l) == l && n.isNumeric(l) && l <= a.length && (c = l - 1)), w.attr({
                    role: "tablist"
                }).addClass(o), b.each(function(i) {
                    var u = n(this),
                        f = i + 1;
                    u.attr({
                        role: "tab",
                        id: "tabset-" + v + "-tab-link-" + f,
                        "aria-controls": "tabset-" + v + "-content-" + f
                    }).addClass(r), i === c ? u.attr("aria-selected", "true").addClass(t) : u.attr("aria-selected", "false")
                }), a.each(function(i) {
                    var r = n(this),
                        f = i + 1;
                    r.attr({
                        href: "#tabset-" + v + "-content-" + f
                    }).addClass(u), i === c && r.addClass(t)
                }), p.each(function(i) {
                    var r = n(this),
                        u = i + 1;
                    r.attr({
                        role: "tabpanel",
                        tabIndex: "-1",
                        id: "tabset-" + v + "-content-" + u
                    }).addClass(e), i === c ? r.attr({
                        "aria-expanded": "true"
                    }).addClass(t) : r.attr({
                        "aria-expanded": "false"
                    })
                }), $tabsetContentLink.each(function(r) {
                    var u = n(this);
                    u.addClass(i).text(a.eq(r).text()).attr({
                        "aria-hidden": "true",
                        href: a.eq(r).attr("href")
                    }), r === c && u.addClass(t)
                });
                n("." + i + ", ." + u).on("click", h, function(f) {
                    var o = n(this),
                        l = o.attr("href").split("#")[1],
                        s, v;
                    if(window.innerWidth<601) 
                    { 
                            f.stopImmediatePropagation(); 
                    }
                    f.preventDefault(), o.hasClass(i) && o.hasClass(t) ? (o.removeClass(t), h.find("." + e + "." + t).removeClass(t).attr({
                        "aria-expanded": "false"
                    }), a.eq(c).addClass(t).closest("." + r).addClass(t).attr({
                        "aria-selected": "true"
                    })) : (h.find("." + u + "." + t).removeClass(t).closest("." + r).removeClass(t).attr({
                        "aria-selected": "false"
                    }), h.find("." + i + "." + t).removeClass(t), h.find("." + u + '[href="#' + l + '"]').addClass(t).closest("." + r).addClass(t).attr({
                        "aria-selected": "true"
                    }), h.find("." + i + '[href="#' + l + '"]').addClass(t), h.find("." + e + "." + t).removeClass(t).attr({
                        "aria-expanded": "false"
                    }), h.find("." + e + "#" + l).attr({
                        "aria-expanded": "true"
                    }).addClass(t), o.hasClass(i) ? (s = n(this).offset().top, v = n(window).scrollTop() >= s ? s - 80 : s, n("html,body").animate({
                        scrollTop: v
                    }, 200, "swing")) : (s = n(this).offset().top-($('.suntrust-header-alt').innerHeight()+15),n("html,body").animate({
                        scrollTop: s
                    }, 200, "swing")))
                });
                y();
                n(window).on("resize", function() {
                    var e = f;
                    y(), e === "accordion" && f === "tabset" && n("." + i + "." + t).length === 0 && (h.find("." + u + "." + t).removeClass(t).closest("." + r).removeClass(t).attr({
                        "aria-selected": "false"
                    }), h.find("." + i + "." + t).removeClass(t), a.eq(c).addClass(t).closest("." + r).addClass(t).attr({
                        "aria-selected": "true"
                    }), $tabsetContentLink.eq(c).addClass(t), p.eq(c).attr({
                        "aria-expanded": "true"
                    }).addClass(t))
                })
            })
        }
    })
})(jQuery);