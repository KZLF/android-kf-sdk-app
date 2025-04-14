package com.m7.imkfsdk.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.holder.XbotFromCityHolder;
import com.m7.imkfsdk.chat.holder.XbotFromDateHolder;
import com.m7.imkfsdk.chat.holder.XbotFromFileHolder;
import com.m7.imkfsdk.chat.holder.XbotFromMulitSelectHolder;
import com.m7.imkfsdk.chat.holder.XbotFromMulitTextHolder;
import com.m7.imkfsdk.chat.holder.XbotFromSingleSelectHolder;
import com.m7.imkfsdk.chat.holder.XbotFromSingleTextHolder;
import com.m7.imkfsdk.chat.holder.XbotHeadNoteHolder;
import com.m7.imkfsdk.chat.holder.XbotSubmitHolder;
import com.m7.imkfsdk.chat.model.Option;
import com.m7.imkfsdk.utils.FIleResourceUtil;
import com.m7.imkfsdk.utils.KFDrowDownUtils;
import com.m7.imkfsdk.utils.MimeTypesTools;
import com.m7.imkfsdk.utils.ToastUtils;
import com.m7.imkfsdk.view.MulitTagView;
import com.m7.imkfsdk.view.bottomselectview.WenChatDropdownSelectView;
import com.m7.imkfsdk.view.dropdownmenu.DropDownMenu;
import com.m7.imkfsdk.view.dropdownmenu.OnMenuSelectedListener;
import com.m7.imkfsdk.view.pickerview.newTimePickerView;
import com.moor.imkf.http.HttpManager;
import com.moor.imkf.model.entity.AddressData;
import com.moor.imkf.model.entity.AddressResult;
import com.moor.imkf.model.entity.UploadFileBean;
import com.moor.imkf.model.entity.XbotForm;
import com.moor.imkf.utils.MoorKFfileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class XbotFormAdapter extends RecyclerView.Adapter{
    private final Context mContext;
    private final List<XbotForm.FormInfoBean> formInfos=new ArrayList<XbotForm.FormInfoBean>();
    private final AddressResult addressResult;



    public int Type_FormSingleText=0;//单行文本
    public int Type_FormMulitText=1;//多行文本
    public int Type_FormSingleSelect=2;//下拉单选
    public int Type_FormMulitSelect=3;//多选
    public int Type_FormFileList=4;//附件
    public int Type_Formdate=5;//日期
    public int Type_FormCity=6;//省市县
    public int Type_Submit=99;//确定按钮
    public int Type_HeadNote=98;//顶部备注部分
    private boolean isDataHasFile=false;//为true 代表原来有文件，这时不再删除接口，只动数据源

    public void setSubmitClickListener(onSubmitClickListener submitClickListener) {
        this.submitClickListener = submitClickListener;
    }

    private onSubmitClickListener submitClickListener;

    public XbotFormAdapter(Context mContext,List<XbotForm.FormInfoBean> formInfos,
                           AddressResult addressResult, boolean isDataHasFile){
        this.formInfos.clear();
        this.formInfos.addAll(formInfos);
        this.mContext=mContext;
        this.addressResult=addressResult;
        this.isDataHasFile=isDataHasFile;

        XbotForm.FormInfoBean bean= new XbotForm.FormInfoBean();
        bean.type=XbotForm.Type_Submit;
        this.formInfos.add(bean);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if(viewType==Type_FormSingleText){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_singletext, viewGroup, false);
            return new XbotFromSingleTextHolder(view);
        }else if(viewType==Type_FormMulitText){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_mulitetext, viewGroup, false);
            return new XbotFromMulitTextHolder(view);
        }else if(viewType==Type_FormSingleSelect){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_singleselect, viewGroup, false);
            return new XbotFromSingleSelectHolder(view);
        }else if(viewType==Type_FormMulitSelect){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_mulitselect, viewGroup, false);
            return new XbotFromMulitSelectHolder(view);
        }else if(viewType==Type_FormFileList){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_file, viewGroup, false);
            return new XbotFromFileHolder(view);
        }else if(viewType==Type_Formdate){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_date, viewGroup, false);
            return new XbotFromDateHolder(view);
        }else if(viewType==Type_FormCity){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_city, viewGroup, false);
            return new XbotFromCityHolder(view);
        }else if(viewType==Type_Submit){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_submit, viewGroup, false);
            return new XbotSubmitHolder(view);
        }else if(viewType==Type_HeadNote){
            View view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_xbot_form_headnote, viewGroup, false);
            return new XbotHeadNoteHolder(view);
        }


        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        int viewType = viewHolder.getItemViewType();
        final XbotForm.FormInfoBean formInfoBean = formInfos.get(i);
        if(formInfoBean!=null){
            if(viewType==Type_FormSingleText){
                XbotFromSingleTextHolder singleTextHolder= (XbotFromSingleTextHolder) viewHolder;

                if(formInfoBean.flag==1){
                    singleTextHolder.tv_required.setVisibility(View.VISIBLE);
                }else{
                    singleTextHolder.tv_required.setVisibility(View.GONE);
                }
                singleTextHolder.tv_title.setText(formInfoBean.name);
                if(TextUtils.isEmpty(formInfoBean.remarks)){
                    singleTextHolder.et_single.setHint(mContext.getString(R.string.ykfsdk_ykf_please_input));
                }else{
                    singleTextHolder.et_single.setHint(formInfoBean.remarks);
                }
                //如果有默认值
                if(!TextUtils.isEmpty(formInfoBean.value)){
                    singleTextHolder.et_single.setText(formInfoBean.value);
                }

                singleTextHolder.et_single.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        formInfoBean.value=s.toString();
                    }
                });

            }else if(viewType==Type_FormMulitText){
                XbotFromMulitTextHolder mulitTextHolder= (XbotFromMulitTextHolder) viewHolder;
                if(formInfoBean.flag==1){
                    mulitTextHolder.tv_required.setVisibility(View.VISIBLE);
                }else{
                    mulitTextHolder.tv_required.setVisibility(View.GONE);
                }
                mulitTextHolder.tv_title.setText(formInfoBean.name);

                if(TextUtils.isEmpty(formInfoBean.remarks)){
                    mulitTextHolder.et_mulit.setHint(mContext.getString(R.string.ykfsdk_ykf_please_input));
                }else{
                    mulitTextHolder.et_mulit.setHint(formInfoBean.remarks);
                }
                //如果有默认值
                if(!TextUtils.isEmpty(formInfoBean.value)){
                    mulitTextHolder.et_mulit.setText(formInfoBean.value);
                }

                mulitTextHolder.et_mulit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        formInfoBean.value=s.toString();
                    }
                });
            }else if(viewType==Type_FormSingleSelect){
                XbotFromSingleSelectHolder singleSelectHolder= (XbotFromSingleSelectHolder) viewHolder;
                if(formInfoBean.flag==1){
                    singleSelectHolder.tv_required.setVisibility(View.VISIBLE);
                }else{
                    singleSelectHolder.tv_required.setVisibility(View.GONE);
                }
                singleSelectHolder.tv_title.setText(formInfoBean.name);


                DropDownMenu drop=singleSelectHolder.drop_single;
                KFDrowDownUtils.setChatDropDownMenu(mContext,drop,formInfoBean.select);
                //默认值
                if(!TextUtils.isEmpty(formInfoBean.value)){
                    for(int b=0;b<formInfoBean.select.length;b++){
                        if(formInfoBean.value.equals(formInfoBean.select[b])){
                            drop.setSelectIndex(b);
                        }
                    }
                }

                drop.setMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onSelected(View listview, int RowIndex, int ColumnIndex) {
                        if(RowIndex<formInfoBean.select.length){
                            formInfoBean.value=formInfoBean.select[RowIndex];
                        }
                    }
                });



            }else if(viewType==Type_FormMulitSelect){
                XbotFromMulitSelectHolder mulitSelectHolder= (XbotFromMulitSelectHolder) viewHolder;
                if(formInfoBean.flag==1){
                    mulitSelectHolder.tv_required.setVisibility(View.VISIBLE);
                }else{
                    mulitSelectHolder.tv_required.setVisibility(View.GONE);
                }
                mulitSelectHolder.tv_title.setText(formInfoBean.name);


                //默认值
                String[] val=new String[]{};
                if(!TextUtils.isEmpty(formInfoBean.value)){
                    val=formInfoBean.value.split(",");
                }

                List<Option> options = new ArrayList<>();
                for (int p=0;p<formInfoBean.select.length;p++) {
                    Option option = new Option();
                    option.name = formInfoBean.select[p];
                    for(int a=0;a<val.length;a++){
                        if(formInfoBean.select[p].equals(val[a])){
                            option.isSelected=true;
                        }
                    }
                    options.add(option);
                }
                mulitSelectHolder.tagView.initTagView(options, 1);
                mulitSelectHolder.tagView.setOnSelectedChangeListener(new MulitTagView.OnSelectedChangeListener() {
                    @Override
                    public void getTagList(List<Option> options) {
                        String str="";
                        if(options!=null){
                            if(options.size()>0){
                                for(int a=0;a<options.size();a++){
                                    str=str+options.get(a).name+",";
                                }
                            }
                        }
                        if(!TextUtils.isEmpty(str)){
                            str=str.substring(0, str.length() -1);
                        }
                        formInfoBean.value=str;
                     }
                });

            }else if(viewType==Type_FormFileList){
                XbotFromFileHolder fromFileHolder= (XbotFromFileHolder) viewHolder;
                if(formInfoBean.flag==1){
                    fromFileHolder.tv_required.setVisibility(View.VISIBLE);
                }else{
                    fromFileHolder.tv_required.setVisibility(View.GONE);
                }
                fromFileHolder.tv_title.setText(formInfoBean.name);
                fromFileHolder.rl_xbot_form_addfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitClickListener.OnAddFileClick(i,formInfoBean);
                    }
                });
                fromFileHolder.ll_xbot_file.removeAllViews();
                for(int f=0;f<formInfoBean.filelist.size();f++){
                    final UploadFileBean fileBean=formInfoBean.filelist.get(f);
                    View view=View.inflate(mContext,R.layout.ykfsdk_kf_xbot_form_fileitem,null);

                    TextView tv_xbot_fileitem_name=view.findViewById(R.id.tv_xbot_fileitem_name);
                    final ImageView iv_delete_file=view.findViewById(R.id.iv_delete_file);
                    ImageView iv_xbot_fileitem_type=view.findViewById(R.id.iv_xbot_fileitem_type);
                    tv_xbot_fileitem_name.setText(fileBean.getName());
                    iv_xbot_fileitem_type.setImageResource(FIleResourceUtil.getFile_Icon(mContext,fileBean.getName()));
                    iv_delete_file.setTag(fileBean);
                    iv_delete_file.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UploadFileBean  bean= (UploadFileBean) iv_delete_file.getTag();
                            if(!isDataHasFile){
                                ArrayList<String> keys=new ArrayList<String>();
//                                String a=bean.getUrl().replace(RequestUrl.QINIU_HTTP,"");
                                String a=bean.getFileKey();
                                keys.add(a);
                                HttpManager.delXbotFormFile(keys,null);
                            }
                            formInfoBean.filelist.remove(bean);
                            notifyItemChanged(i,formInfoBean);
                         }
                    });

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent intent = new Intent();
                                File file = new File(fileBean.getLocalUrl());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri contentUri = MoorKFfileUtils.fileToUri(file);
                                    intent.setDataAndType(contentUri,  MimeTypesTools.getMimeType(mContext, fileBean.getLocalUrl()));
                                } else {
                                    intent.setDataAndType(Uri.fromFile(file),  MimeTypesTools.getMimeType(mContext, fileBean.getLocalUrl()));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                                 mContext.startActivity(Intent.createChooser(intent, null));
                            } catch (Exception e) {
                            }

                        }
                    });
                    fromFileHolder.ll_xbot_file.addView(view);
                }



            }else if(viewType==Type_Formdate){
                final XbotFromDateHolder dateHolder= (XbotFromDateHolder) viewHolder;
                if(formInfoBean.flag==1){
                    dateHolder.tv_required.setVisibility(View.VISIBLE);
                }else{
                    dateHolder.tv_required.setVisibility(View.GONE);
                }
                dateHolder.tv_title.setText(formInfoBean.name);
                if(TextUtils.isEmpty(formInfoBean.remarks)){
                    dateHolder.tv_date.setHint(mContext.getString(R.string.ykfsdk_ykf_please_input));
                }else{
                    dateHolder.tv_date.setHint(formInfoBean.remarks);
                }

                //默认值
                if(!TextUtils.isEmpty(formInfoBean.value)){
                    dateHolder.tv_date.setText(formInfoBean.value);
                }

                dateHolder.tv_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newTimePickerView pickerView = showDataDialogByDate(mContext, null);
                        pickerView.setListener(new newTimePickerView.OnTimeSelectListener() {
                            @Override
                            public void onTimeSelect(Date date, View v) {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                dateHolder.tv_date.setText(format.format(date));
                                formInfoBean.value=format.format(date);
                            }
                        });
                        pickerView.show();
                    }
                });



            }else if(viewType==Type_FormCity){
                final XbotFromCityHolder cityHolder= (XbotFromCityHolder) viewHolder;
                if(formInfoBean.flag==1){
                    cityHolder.tv_required.setVisibility(View.VISIBLE);
                }else{
                    cityHolder.tv_required.setVisibility(View.GONE);
                }
                cityHolder.tv_title.setText(formInfoBean.name);

                if(TextUtils.isEmpty(formInfoBean.remarks)){
                    cityHolder.tv_city.setHint(mContext.getString(R.string.ykfsdk_ykf_please_input));
                }else{
                    cityHolder.tv_city.setHint(formInfoBean.remarks);
                }
                //默认值
                if(!TextUtils.isEmpty(formInfoBean.value)){
                    cityHolder.tv_city.setText(formInfoBean.value);
                }

                if(addressResult!=null){
                    cityHolder.tv_city.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WenChatDropdownSelectView wenChatDropdownSelectView = new WenChatDropdownSelectView
                                    (mContext,
                                            addressResult, 3);
                            wenChatDropdownSelectView.show();
                            wenChatDropdownSelectView.setOnClickListent(new WenChatDropdownSelectView.OnClickItemListent() {

                                @Override
                                public void save(List<AddressData> options) {
                                    String str="";
                                    if(options!=null){
                                        if(options.size()>0){
                                            for(int a=0;a<options.size();a++){
                                                    str=str+options.get(a).label+"-";
                                            }
                                        }
                                    }
                                    if(!TextUtils.isEmpty(str)){
                                        str=str.substring(0, str.length() -1);
                                    }
                                    cityHolder.tv_city.setText(str);
                                    formInfoBean.value=str;
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                        }
                    });
                }

            }else if(viewType==Type_HeadNote){
                XbotHeadNoteHolder xbotHeadNoteHolder= (XbotHeadNoteHolder) viewHolder;
                xbotHeadNoteHolder.tv_formNotes.setText(formInfoBean.name);
            }else if(viewType==Type_Submit){
                XbotSubmitHolder xbotSubmitHolder= (XbotSubmitHolder) viewHolder;
                xbotSubmitHolder.bt_form_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<XbotForm.FormInfoBean> list=new ArrayList<XbotForm.FormInfoBean>();
                        list.addAll(formInfos);

                        list.remove(list.size()-1);
                        for(int q=0;q<list.size();q++){
                            XbotForm.FormInfoBean subinfo=list.get(q);
                            if(subinfo.type.equals(XbotForm.Type_DataFile)){
                                String filevalue="";
                                for(int f=0;f<subinfo.filelist.size();f++){
                                    filevalue=filevalue+"<a href='"+subinfo.filelist.get(f).getUrl()+"'target='_blank'>"+
                                            subinfo.filelist.get(f).getName()+"</a>,";
                                    subinfo.filelist.get(f).setLocalUrl(null);
                                }
                                if(!TextUtils.isEmpty(filevalue)){
                                    filevalue=filevalue.substring(0, filevalue.length() -1);
                                }
                                subinfo.value=filevalue;
                            }
                            if(subinfo.flag==1){
                                //必填项是否有值
                                if(TextUtils.isEmpty(subinfo.value)){
                                    ToastUtils.showShort(mContext,subinfo.name+mContext.getString(R.string.ykfsdk_ykf_required_form));
                                    return;
                                }
                            }
                        }
                        submitClickListener.OnSubmitClick(list);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return formInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (XbotForm.Type_DataSingleText
                .equals(formInfos.get(position).type)) {
            return Type_FormSingleText;
        } else if (XbotForm.Type_DataMulitText
                .equals(formInfos.get(position).type)) {
            return Type_FormMulitText;
        } else if (XbotForm.Type_DataSingleSelect
                .equals(formInfos.get(position).type)) {
            return Type_FormSingleSelect;
        }else if (XbotForm.Type_DataMulitSelect
                .equals(formInfos.get(position).type)) {
            return Type_FormMulitSelect;
        }else if (XbotForm.Type_Datadate
                .equals(formInfos.get(position).type)) {
            return Type_Formdate;
        }else if (XbotForm.Type_DataFile
                .equals(formInfos.get(position).type)) {
            return Type_FormFileList;
        }else if (XbotForm.Type_DataCity
                .equals(formInfos.get(position).type)) {
            return Type_FormCity;
        }else if (XbotForm.Type_Submit
                .equals(formInfos.get(position).type)) {
            return Type_Submit;
        }else if(XbotForm.Type_HeadNote
            .equals(formInfos.get(position).type)){
            return Type_HeadNote;
        }else {
            return 0;
        }
    }








    /**
     * 显示日期弹框
     *
     * @param context
     * @return
     */
    public static newTimePickerView showDataDialogByDate(Context context, Calendar calendar) {
        Calendar cal = Calendar.getInstance();
        if (calendar != null) {
            cal = calendar;
        }
        //时间选择器
        newTimePickerView pvTime = new newTimePickerView
                .Builder(context)
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel(context.getString(R.string.ykfsdk_pickerview_year), context.getString(R.string.ykfsdk_pickerview_month)
                        , context.getString(R.string.ykfsdk_pickerview_day), ""
                        , "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(cal)
                .setRangDate(Calendar.getInstance(), Calendar.getInstance())
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
        pvTime.setDate(cal);
        return pvTime;
    }


    public interface onSubmitClickListener {
        void OnSubmitClick(List<XbotForm.FormInfoBean> formInfos);
        void OnAddFileClick(int position ,XbotForm.FormInfoBean formInfoBean);
    }


}
