(ns editor-clj.doubly-linked-list)

(defprotocol IDoublyLL
  (nexts [self])
  (prevs [self])
  (curr [self])
  (add-after [self x])
  (add-before [self x])
  (set-curr [self x])
  (pick-next [self])
  (pick-prev [self])
  (pop-next [self])
  (pop-prev [self])
  (pop-curr [self])
  (can-go-forward? [self])
  (can-go-backward? [self])
  (forward [self])
  (backward [self]))

(defrecord PersistentDoublyLL
    [nexts prevs curr]
  Object
  (toString [self]
    (str "("
         (reduce #(str %1 " " %2) "" (reverse prevs))
         " [ " curr " ]"
         (reduce #(str %1 " " %2) "" nexts)
         ")")) 
  IDoublyLL
  (nexts [self]
    nexts)
  (prevs [self]
    prevs)
  (curr [self]
    curr)
  (add-after [self x]
    (cons x self))
  (add-before [self x]
    (PersistentDoublyLL. nexts
                         (conj prevs x)
                         curr))
  (set-curr [self x]
    (PersistentDoublyLL. nexts
                         prevs
                         x))
  (pick-next [self]
    (first nexts))
  (pick-prev [self]
    (first prevs))
  (pop-next [self]
    (PersistentDoublyLL. (pop nexts)
                         prevs
                         curr))
  (pop-prev [self]
    (PersistentDoublyLL. nexts
                         (pop prevs)
                         curr))
  (pop-curr [self]
    (PersistentDoublyLL. (pop nexts)
                         prevs
                         (first nexts)))
  (can-go-forward? [self]
    (not (empty? nexts)))
  (can-go-backward? [self]
    (not (empty? prevs)))
  (forward [self]
    (PersistentDoublyLL. (pop nexts)
                         (conj prevs curr)
                         (first nexts)))
  (backward [self]
    (PersistentDoublyLL. (conj nexts curr)
                         (pop prevs)
                         (first prevs))))

(defmethod clojure.core/print-method PersistentDoublyLL [x writer]
  (.write writer (.toString x)))

(defn dbl
  "Constructs a doubly-linked list, where s is a sequence to start off"
  [s]
  (let [s (into '() s)]
    (PersistentDoublyLL. (pop s)
                         '()
                         (peek s))))

