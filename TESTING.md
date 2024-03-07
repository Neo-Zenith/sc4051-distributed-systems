| Server                      | Client      | Expected                                                                                   | Tested |
| --------------------------- | ----------- | ------------------------------------------------------------------------------------------ | ------ |
| at-most-once - normal       | normal      | normal transmission                                                                        | /      |
| at-most-once - normal       | packet loss | client retransmit                                                                          | /      |
| at-most-once - packet loss  | normal      | client timeout -> retransmit (dup filtering - no recalculate on server side)               | /      |
| at-most-once - packet loss  | packet loss | client retransmit -> timeout -> retransmit (dup filtering - no recalculate on server side) | /      |
| at-least-once - normal      | normal      | normal transmission                                                                        | /      |
| at-least-once - normal      | packet loss | client retransmit                                                                          | /      |
| at-least-once - packet loss | normal      | client timeout -> retransmit (recalculate on server side)                                  | /      |
| at-least-once - packet loss | packet loss | client retransmit -> timeout -> retransmit (recalculate on server side)                    | /      |
