# Java DSA Practice Repo

Personal repo for practising data structures & algorithms in Java, plus some
standalone Java exercises (streams, LRU cache, consistent hashing) at the root.

The DSA work lives in [DSA/](DSA/) as `Problem<N>.java`, worked in numeric order.

## How these problem files are structured

Every `Problem<N>.java` follows the same shape:

1. **A long comment block at the top** — the problem statement, worked examples,
   the intended approach, the traps, and the target time/space complexity.
2. **Solution stub(s)** marked `// TODO` — the part I fill in.
3. **A self-checking test harness in `main`** — scripted cases, edge cases, a
   randomised cross-check against a brute-force reference, and a performance
   check that would time out if the solution were not the required complexity.

Run any of them directly (single-file source mode, no compile step):

```
cd DSA
java Problem14.java
```

The harness prints PASS/FAIL per case. A correct solution passes everything
including the random cross-check and the perf check.

## How I want to be helped

**Give me stubs and explanations, not finished solutions.** When I ask for the
next problem, write the comment block + test harness + `// TODO` stub and let me
write the algorithm. I'll ask for review when I'm done.

**Review honestly.** If my code passes the tests for the wrong reason, say so
plainly rather than congratulating me. This already happened once and it was the
most useful feedback in the session:

> In Problem 12 my "two stacks" queue passed all 500 random tests, but only
> because I used `ArrayDeque.add()` (tail) with `.pop()` (head) — which is just
> a queue. The `in` stack was dead code. Tests green, lesson missed.

So: check that the solution actually uses the intended technique, not just that
the output matches.

**Explain concepts when I ask** ("how to use X, when to use X") before diving
into code review. Tables and worked traces help more than prose.

## Conventions

- One problem per file; **don't** bundle several problems as Part A / B / C in a
  single file. (Problems 1–11 use the A/B/C style; from 12 onward it's one
  problem per file.)
- `ArrayDeque` over the legacy `Stack` class.
- Test harnesses must fail loudly against an empty stub — no vacuous passes.

## Progress

| File | Problem | Status |
|---|---|---|
| Problem1–9 | earlier topics | done |
| Problem10 | Monotonic stack (next greater, daily temperatures) | done |
| [Problem11.java](DSA/Problem11.java) | Stacks II — A: Min Stack, B: Evaluate RPN, C: Largest Rectangle in Histogram | **done, all pass** |
| [Problem12.java](DSA/Problem12.java) | Implement Queue using Stacks | **done, all pass** |
| [Problem13.java](DSA/Problem13.java) | Design Circular Queue (ring buffer) | **stub — not started** |
| [Problem14.java](DSA/Problem14.java) | Sliding Window Maximum (monotonic deque) | **stub — attempt in progress, current two-pointer approach is wrong; needs the deque** |

Current topic thread: **stacks → queues → deques**. Problems 10–11 covered the
monotonic stack; 12–14 extend it to queues and the monotonic deque.

## Gotchas already hit (don't need re-explaining, but worth remembering)

- `ArrayList.remove(int)` removes by **index**, not by value. For a stack, always
  `list.remove(list.size() - 1)`.
- String comparison must use `.equals()`, never `==`.
- **`Deque`: `push` adds at the HEAD, `add` adds at the TAIL.** They sound like
  synonyms and are opposites. When both ends are in play, use the explicit
  `addFirst` / `addLast` / `pollFirst` / `pollLast` / `peekFirst` / `peekLast`
  names so wrong code looks wrong.
- `poll*` / `peek*` return `null` on empty (→ NPE when unboxing to `int`);
  `remove*` / `get*` / `pop` throw. Guard with `isEmpty()`.
- Min Stack needs a **second stack** popped in lockstep — a single `int min`
  cannot recover the previous minimum after a pop.
- Largest-rectangle / sliding-window loops need the **sentinel final iteration**
  (`i <= n`) to flush whatever is still on the stack.
