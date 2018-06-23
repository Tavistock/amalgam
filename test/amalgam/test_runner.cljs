(ns amalgam.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [amalgam.core-test]))

(doo-tests 'amalgam.core-test)
