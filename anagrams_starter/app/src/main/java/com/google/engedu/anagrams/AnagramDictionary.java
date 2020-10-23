/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 2;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private List<String> wordList =  new ArrayList<String>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList> lettersToWord = new HashMap<>();
    private  HashMap<Integer, ArrayList> sizeToWords = new HashMap<>();
    private int wordLength = DEFAULT_WORD_LENGTH;
    private static final String TAG = "MyActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            this.wordList.add(word);
            this.wordSet.add(word);
            ArrayList<String> wordList = this.sizeToWords.getOrDefault(word.length(), new ArrayList<>());
            wordList.add(word);
            this.sizeToWords.put(word.length(), wordList);
            String sortedString  = this.sortWord(word);
            ArrayList<String> wordList1 = this.lettersToWord.getOrDefault(sortedString, new ArrayList());
            wordList1.add(word);
            this.lettersToWord.put(sortedString, wordList1);
        }
        Iterator it = lettersToWord.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList> entry = (Map.Entry<String, ArrayList>) it.next();
            List<String> value = entry.getValue();

            if (value.size() < MIN_NUM_ANAGRAMS) {
                for (String word: value) {
                    wordList.remove(word);
                }
                it.remove();
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        if (wordSet.contains(word)) {
            for (int i = 0; i < word.length()-base.length(); i++) {
                if (word.substring(i, i + base.length()).equals(base)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<String> getAnagrams(String targetWord) {
        List<String> sortedString = Arrays.asList(targetWord.split(""));
        Collections.sort(sortedString);
        String res = String.join("", sortedString);
        return this.lettersToWord.getOrDefault(res, new ArrayList() );
    }

    // Update to allow adding 2 more letters
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                String first = Character.toString((char)(i + 97));
                String second = Character.toString((char)(j + 97));
                List<String> sortedString = Arrays.asList((word + first + second).split(""));
                Collections.sort(sortedString);
                String res = String.join("", sortedString);
                List<String> tempArr = this.lettersToWord.getOrDefault(res,  new ArrayList());
                result.addAll(tempArr);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String pickGoodStarterWord() {
        Random random = new Random();
        String startWord = this.wordList.get(random.nextInt(wordList.size()));
        String sortedStartWord = this.sortWord(startWord);
        while (this.lettersToWord.get(sortedStartWord).size() < MIN_NUM_ANAGRAMS) {
            startWord = this.wordList.get(random.nextInt(this.wordList.size()));
            sortedStartWord = this.sortWord(startWord);
        }
        if (this.wordLength < MAX_WORD_LENGTH) {
            this.wordLength += 1;
        }
        else {
            this.wordLength = DEFAULT_WORD_LENGTH;
        }
        return startWord;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String sortWord(String word) {
        List<String> sortedString = Arrays.asList(word.split(""));
        Collections.sort(sortedString);
        String res = String.join("", sortedString);
        return res;
    }
}
