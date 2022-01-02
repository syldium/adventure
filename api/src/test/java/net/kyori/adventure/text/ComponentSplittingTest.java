/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2022 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.Style.style;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentSplittingTest {

  private final Pattern newLinePattern = Pattern.compile("\n");

  @Test
  public void noDelimiter() {
    final Component text = text("Some text on a single line");
    assertEquals(Collections.singletonList(text), text.split(this.newLinePattern));
  }

  @Test
  public void oneDelimiter() {
    final List<Component> expected = Arrays.asList(text("A line"), text("and another"));
    assertEquals(expected, text("A line\nand another").split(this.newLinePattern));
  }

  @Test
  public void inheritStyle() {
    final List<Component> expected = Arrays.asList(
      text("A line", NamedTextColor.GREEN).append(text("of text")),
      text("and another", NamedTextColor.GREEN)
    );
    assertEquals(
      expected,
      text()
        .content("A line")
        .color(NamedTextColor.GREEN)
        .append(
          text("of text\nand another")
        )
        .build()
        .split(this.newLinePattern)
    );
  }

  @Test
  public void depth() {
    final Style style = style(TextDecoration.ITALIC);
    final List<Component> expected = Arrays.asList(
      text("ad", style),
      text("ven", style),
      text("ture", style)
    );
    assertEquals(
      expected,
      text()
        .content("ad-")
        .style(style)
        .append(
          text().content("ven").append(text("-ture"))
        )
        .build()
        .split(Pattern.compile("-"))
    );
  }

  @Test
  public void multipleChildren() {
    final List<Component> expected = Arrays.asList(
      translatable()
        .key("translatable.key")
        .decoration(TextDecoration.BOLD, true)
        .append(text("one", NamedTextColor.GOLD))
        .build(),
      text(" two", style(NamedTextColor.GOLD, TextDecoration.BOLD)),
      text("three", style(NamedTextColor.RED, TextDecoration.BOLD)),
      text(" four", style(NamedTextColor.RED, TextDecoration.BOLD)),
      text()
        .append(
          text("five and six", style(NamedTextColor.AQUA, TextDecoration.BOLD)),
          text("seven and eight", style(TextDecoration.ITALIC))
        )
        .build()
    );
    assertEquals(
      expected,
      translatable()
        .key("translatable.key")
        .decoration(TextDecoration.BOLD, true)
        .append(
          text("one, two", NamedTextColor.GOLD),
          text(",", NamedTextColor.GRAY),
          text("three, four", NamedTextColor.RED),
          text(",", NamedTextColor.GRAY),
          text("five and six", NamedTextColor.AQUA),
          text("seven and eight", style(TextDecoration.ITALIC))
        )
        .build()
        .split(Pattern.compile(","))
    );
  }

  @Test
  public void version() {
    final List<Component> expected = Arrays.asList(
      text("4"),
      text("10"),
      text()
        .content("0")
        .append(
          text("-SNAPSHOT"),
          text("version")
        )
        .build()
    );
    assertEquals(
      expected,
      text()
        .content("4.10.0")
        .append(
          text("-SNAPSHOT"),
          text("version")
        )
        .build()
        .split(Pattern.compile("\\."))
    );
  }

  @Test
  public void multiple() {
    final List<Component> expected = Arrays.asList(
      text("A"),
      text("very"),
      empty(),
      empty(),
      text("important"),
      text("text")
    );
    assertEquals(
      expected,
      text("A very   important text").split(Pattern.compile(" "))
    );
  }

  @Test
  public void nested() {
    final List<Component> expected = Arrays.asList(
      text()
        .content("Text")
        .append(
          text()
            .content("aqua")
            .color(NamedTextColor.AQUA)
        )
        .build(),
      text()
        .append(
          text("blue", NamedTextColor.BLUE)
            .append(text("colored")),
          text("white"),
          text("red", NamedTextColor.RED)
        )
        .build()
    );
    assertEquals(
      expected,
      text()
        .content("Text")
        .append(
          text()
            .content("aqua")
            .color(NamedTextColor.AQUA)
            .append(text("/blue", NamedTextColor.BLUE)
              .append(text("colored")))
            .build(),
          text("white"),
          text("red", NamedTextColor.RED)
        )
        .build()
        .split(Pattern.compile("/"))
    );
  }
}
