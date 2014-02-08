(ns breakout.core)

(defn get-background-color []
  (.-backgroundColor (js/getComputedStyle (.-body js/document)))
  )

(defn clear [width height context]
    (set! (.-fillStyle context) (get-background-color))
    (.fillRect context 0 0 width height)
    (.beginPath context)
    (.rect context 0 0 width height)
    (.stroke context)
  )

(defmulti draw :type)

(defmethod draw :block [object context]
  (let [{ color :color width :width height :height top :top left :left } object]
    (.beginPath context)
    (.rect context left top width height)
    (set! (.-fillStyle context) color)
    (.fill context)
    (.stroke context)
    ))

(defmethod draw :bat [object context]
  (let [{ color :color width :width height :height left :left } object canvas (.getElementById js/document "canvas") canvasHeight (.-height canvas)]
    (set! (.-fillStyle context) color)
    (.fillRect context left (- canvasHeight height) width height)
    ))

(defmethod draw :ball [object context]
  (let [{ color :color radius :radius x :x y :y } object]
    (.beginPath context)
    (.arc context x y radius 0 (* 2 3.14159))
    (set! (.-fillStyle context) color)
    (.fill context)
    ))

(defmethod draw :empty [object context] nil)

(defn block-line [state width top]
  (let [
        block-width (/ width (count state))
        assocfn #(assoc {:type :block :color %1 :top top :width block-width :height 20 } :left (* %2 block-width))
        ]
  (map #(if (get % 0) (assocfn (get % 0) (get % 1)) { :type :empty }) (map vector state (range)))
  ))

(defn blocks [state width]
  (reduce concat [] (map #(block-line (get % 0) width (+ 40 (* (get % 1) 20))) (map vector state (range))))
  )

(def ball { :type :ball :color "white" :radius 5 :x 200 :y 200 })

(def bat { :type :bat :color "black":left 50 :width 50 :height 20 })

(defn tick []
  (.log js/console "tick"))

(defn ^:export init []
 (let [
       canvas (.getElementById js/document "canvas")
       context (.getContext canvas "2d")
       width (.-width canvas)
       height (.-height canvas)
       state [["green" "blue" "yellow" "red" "green"]
              ["green" "blue" "yellow" "red" "green"]
              ["green" "blue" "yellow" "red" "green"]
              ["green" "blue" "yellow" "red" "green"]
              ["yellow" "green" "blue" "red" "green"]]
       ]
   (clear width height context)
   (doseq [object (concat (blocks state width) [ball bat])] (draw object context))

   (js/setInterval tick 1000)

   ))







