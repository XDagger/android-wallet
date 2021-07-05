package io.xdag.xdagwallet.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.VerifyBackupMnemonicWordsAdapter;
import io.xdag.xdagwallet.adapter.VerifyBackupSelectedMnemonicWordsAdapter;
import io.xdag.xdagwallet.verify.VerifyMnemonicWordTag;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


// 验证 助记词是否 正确界面

public class VerifyMnemonicBackupActivity extends BaseActivity {
    private static final int VERIFY_SUCCESS_RESULT = 2202;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_selected)
    RecyclerView rvSelected;
    @BindView(R.id.rv_mnemonic)
    RecyclerView rvMnemonic;
    private String walletMnemonic;

    private List<VerifyMnemonicWordTag> mnemonicWords;

    private List<String> selectedMnemonicWords;

    private VerifyBackupMnemonicWordsAdapter verifyBackupMenmonicWordsAdapter;
    private VerifyBackupSelectedMnemonicWordsAdapter verifyBackupSelectedMnemonicWordsAdapter;
    private long walletId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("备份助记词 ");
    }

    @Override
    public void initDatas() {
        walletMnemonic = getIntent().getStringExtra("walletMnemonic");

        Log.d("VerifyMnemonicBackUp", "walletMnemonic:" + walletMnemonic);

        String[] words = walletMnemonic.split("\\s+");
        mnemonicWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            VerifyMnemonicWordTag verifyMnemonicWordTag = new VerifyMnemonicWordTag();
            verifyMnemonicWordTag.setMnemonicWord(words[i]);
            mnemonicWords.add(verifyMnemonicWordTag);
        }
        // 乱序
        Collections.shuffle(mnemonicWords);

        // 未选中单词
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        rvMnemonic.setLayoutManager(layoutManager);
        verifyBackupMenmonicWordsAdapter = new VerifyBackupMnemonicWordsAdapter(R.layout.list_item_mnemoic, mnemonicWords);
        rvMnemonic.setAdapter(verifyBackupMenmonicWordsAdapter);


        // 已选中单词
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(this);
        layoutManager2.setFlexWrap(FlexWrap.WRAP);
        layoutManager2.setAlignItems(AlignItems.STRETCH);
        rvSelected.setLayoutManager(layoutManager2);
        selectedMnemonicWords = new ArrayList<>();
        verifyBackupSelectedMnemonicWordsAdapter = new VerifyBackupSelectedMnemonicWordsAdapter(R.layout.list_item_mnemoic_selected, selectedMnemonicWords);
        rvSelected.setAdapter(verifyBackupSelectedMnemonicWordsAdapter);
    }

    @Override
    public void configViews() {
        verifyBackupMenmonicWordsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String mnemonicWord = verifyBackupMenmonicWordsAdapter.getData().get(position).getMnemonicWord();
                if (verifyBackupMenmonicWordsAdapter.setSelection(position)) {
                    verifyBackupSelectedMnemonicWordsAdapter.addData(mnemonicWord);
                }
            }
        });
        verifyBackupSelectedMnemonicWordsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                List<VerifyMnemonicWordTag> datas = verifyBackupMenmonicWordsAdapter.getData();
                for (int i = 0; i < datas.size(); i++) {
                    if (TextUtils.equals(datas.get(i).getMnemonicWord(), verifyBackupSelectedMnemonicWordsAdapter.getData().get(position))) {
                        verifyBackupMenmonicWordsAdapter.setUnselected(i);
                        break;
                    }
                }
                verifyBackupSelectedMnemonicWordsAdapter.remove(position);
            }

        });

    }

    @OnClick(R.id.btn_confirm)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                Log.d("VerifyMnemonicBackUp", "Click!!");
                List<String> data = verifyBackupSelectedMnemonicWordsAdapter.getData();
                int size = data.size();
                if (size == 12) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        stringBuilder.append(data.get(i));
                        if (i != size - 1) {
                            stringBuilder.append(" ");
                        }
                    }
                    String verifyMnemonic = stringBuilder.toString();
                    String trim = verifyMnemonic.trim();
                    Log.d("VerifyMnemonicBackUp", "verifyMnemonic:" + verifyMnemonic);
                    Log.d("VerifyMnemonicBackUp", "trim:" + trim);
                    if (TextUtils.equals(trim, walletMnemonic)) {
                        // TODO 修改该钱包备份标识
                        //WalletDaoUtils.setIsBackup(walletId);
//                        AppManager.getAppManager().finishActivity(MnemonicBackupActivity.class);
                        setResult(VERIFY_SUCCESS_RESULT, new Intent());
                        finish();
                    } else {
                        ToastUtil.show("备份失败，请检查你的助记词");
                    }
                } else {
                    ToastUtil.show("备份失败，请检查你的助记词");
                }
                break;
        }
    }
}
